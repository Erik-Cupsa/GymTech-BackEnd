package ca.mcgill.ecse321.gymregistration.service;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    InstructorRegistrationRepository instructorRegistrationRepository;
    @Autowired
    CustomerRegistrationRepository customerRegistrationRepository;

    /**
     * Create Session: creates a new session
     * @param classType: the classtype of the session
     * @param startTime: the start time of the class
     * @param endTime: the end time of the class
     * @param date: the day the class takes place
     * @param description: a textual description of the class
     * @param name: the name of this specific class
     * @param location: where this class is being taught
     * @param capacity: the maximum number of people that can attend this class
     * @param gymUser: the user creating the session
     * @return the created session
     * @throws GRSException invalid creation request
     */
    @Transactional
    public Session createSession(Date date, Time startTime, Time endTime, String description, String name, String location, ClassType classType, int capacity, GymUser gymUser){
        if(gymUser instanceof Customer) {
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Customers are not allowed to create sessions.");}
        if(sessionRepository.findSessionByStartTimeAndDate(startTime, date)!=null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Time not available.");}
        if(classType == null || startTime == null || endTime == null || date == null || description == null || description.length() == 0 || name == null || name.length() == 0 || location == null || location.length() == 0){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Missing information.");}
        if(!classType.getIsApproved()) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "Class must be approved.");}

        LocalDateTime currentDateTime = LocalDateTime.now();    //getting current date and time
        LocalDateTime sessionDatetime = LocalDateTime.of(date.toLocalDate(), startTime.toLocalTime());  //converting startTime and date to LocalDateTime
        Duration timeDifference = Duration.between(currentDateTime, sessionDatetime);   //calculating difference
        //checking if session is at least 48 hours ahead of current time
        if (timeDifference.toHours() < 48){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Session must be at least 48 hours ahead of the current time.");}
        Session session = new Session(date, startTime, endTime, description, name, location, classType, capacity);
        sessionRepository.save(session);
        return session;
    }


    /**
     * UpdateSession: update a session
     * @param oldSessionId: session to be updated
     * @param newSession: new session to be changed to 
     * @param gymUser: the user updating the session
     * @return the new updated Session
     * @throws GRSException invalid session update request
     **/
    @Transactional
     public Session updateSession(int oldSessionId, Session newSession, GymUser gymUser){
        if(sessionRepository.findSessionById(oldSessionId) == null){
            throw new GRSException(HttpStatus.CONFLICT, "Session with id does not exist.");
        }
        if(newSession == null || newSession.getDate() == null || newSession.getStartTime() == null || newSession.getEndTime() == null || newSession.getDescription() == null || newSession.getName() == null || newSession.getLocation() == null || newSession.getClassType() == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Missing information.");
        }
        if(gymUser instanceof Owner == false && instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(gymUser.getId(), oldSessionId) == null) {
            throw new GRSException(HttpStatus.UNAUTHORIZED, "User does not have access to update session.");
        }
        if(!newSession.getClassType().getIsApproved()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Class must be approved.");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime sessionDatetime = LocalDateTime.of(newSession.getDate().toLocalDate(), newSession.getStartTime().toLocalTime());
        Duration timeDifference = Duration.between(currentDateTime, sessionDatetime);
        //checking if session is at least 48 hours ahead of current time
        if (timeDifference.toHours() < 48){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Session must be at least 48 hours ahead of the current time.");
        }
        Session toUpdate = sessionRepository.findSessionById(oldSessionId);
        toUpdate.setDate(newSession.getDate());
        toUpdate.setStartTime(newSession.getStartTime());
        toUpdate.setEndTime(newSession.getEndTime());
        toUpdate.setDescription(newSession.getDescription());
        toUpdate.setName(newSession.getName());
        toUpdate.setLocation(newSession.getLocation());
        toUpdate.setClassType(newSession.getClassType());
        toUpdate.setCapacity(newSession.getCapacity());
        toUpdate.setId(oldSessionId);
        sessionRepository.save(toUpdate);
        return toUpdate;
    }

    /**
     * GetSessionById: get a session by its id
     * @param id: id of the session to be found
     * @return the session with the given id
     * @throws GRSException session not found
     */
    @Transactional
     public Session getSessionById(int id){
        Session session = sessionRepository.findSessionById(id);
        if (session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        }
        return session;
    }

    /**
     * GetAllSessions: get all sessions in the system
     * @return list of all the sessions
     * @throws GRSException no sessions found
     */
    @Transactional
     public List<Session> getAllSessions(){
        List<Session> sessions = sessionRepository.findAll();
        if(sessions.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No Sessions found in the system.");
        }
        return sessions;
    }

    /**
     * DeleteSession: delete a session by its id
     * @param id: id of the session to be deleted
     * @param gymUser: the user deleting the session
     * @throws GRSException session not found, unauthorized user
     */
    @Transactional
    public void deleteSession(int id, GymUser gymUser){
        if(gymUser instanceof Customer){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Customers can not delete sessions.");
        }
        if(gymUser instanceof Instructor){
            List<InstructorRegistration> instructorRegistrations = instructorRegistrationRepository.findInstructorRegistrationsBySession_id(id);
            for(InstructorRegistration ir: instructorRegistrations){
                if(ir.getInstructor().getEmail() == gymUser.getEmail()){
                    sessionRepository.deleteSessionById(id);
                    return;
                }
            }
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Instructor does not have access to delete.");
        }
        Session session = sessionRepository.findSessionById(id);
        if(session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        }
        instructorRegistrationRepository.deleteInstructorRegistrationsBySession_Id(id);
        try{ //may be that there are no registrations yet
        customerRegistrationRepository.deleteCustomerRegistrationsBySession_Id(id);
        }
        catch(Exception exception)
        {
            System.out.println("may not exist or may be empty");
        }
        finally{
        sessionRepository.deleteSessionById(id);
        }
    }

}
