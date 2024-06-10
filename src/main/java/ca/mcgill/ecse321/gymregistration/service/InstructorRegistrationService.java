package ca.mcgill.ecse321.gymregistration.service;

import java.util.List;

import ca.mcgill.ecse321.gymregistration.dto.InstructorRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.InstructorRegistration;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Session;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import jakarta.transaction.Transactional;


@Service
public class InstructorRegistrationService {

    @Autowired
    InstructorRepository instructorRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    InstructorRegistrationRepository instructorRegistrationRepository;
    @Autowired
    OwnerRepository ownerRepository;

    /**
     * RegisterInstuctorForClass: register an instructor for a class
     * @param sessionId: id of the session
     * @param email: email of the instructor
     * @param gymUser: the user registering the instructor
     * @return the new registration
     * @throws GRSException invalid email or already registered
     */
    @Transactional
    public InstructorRegistration registerInstructorForClass(int sessionId, String email, GymUser gymUser) {
        if(!(gymUser instanceof Owner) && !gymUser.getEmail().equals(email)) {
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Instructors can only be assigned by themselves or by the owner.");
        }
        Instructor instructor = instructorRepository.findInstructorByEmail(email);
        Session session = sessionRepository.findSessionById(sessionId);
        if (instructor == null) {
            throw new GRSException(HttpStatus.NOT_FOUND, "Instructor not found.");
        }
        if (session == null)
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        
        InstructorRegistration instructorRegistration = instructorRegistrationRepository
                .findInstructorRegistrationByInstructor_idAndSession_id(instructor.getId().intValue(), session.getId());
        if (instructorRegistration != null)
            throw new GRSException(HttpStatus.BAD_REQUEST, "Already registered.");

        instructorRegistration = new InstructorRegistration(null, instructor, session);
        instructorRegistrationRepository.save(instructorRegistration);
        return instructorRegistration;
    }

    /**
     * RemoveInstructorFromClass: remove an instructor from a class
     * @param sessionId: id of the session
     * @param email: email of the instructor
     * @param gymUser: the user removing the instructor
     * @throws GRSException not enough instructors registered, Unauthorized user, Instructor not teaching course
     */
    @Transactional

    public void removeInstructorFromClass(int sessionId, String email, GymUser gymUser) {
        List<InstructorRegistration> instructorRegistrations = instructorRegistrationRepository
                .findInstructorRegistrationsBySession_id(sessionId);

        if (instructorRegistrations.size()==0)
            throw new GRSException(HttpStatus.BAD_REQUEST, "Sessions not found.");
        if (instructorRegistrations.size() < 2)
            throw new GRSException(HttpStatus.BAD_REQUEST, "Not enough instructors registered.");
        GymUser gymuser = instructorRepository.findInstructorById(gymUser.getId().intValue());
        if(gymuser ==null)
            gymuser = ownerRepository.findOwnerById(gymUser.getId().intValue());
    
        if( gymuser == null || gymuser instanceof Owner == false || instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(gymuser.getId(), sessionId)==null)
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Cannot remove Instructor.");
        for (InstructorRegistration r : instructorRegistrations) {
            if (r.getInstructor().getId() == instructorRepository.findInstructorByEmail(email).getId()){
                instructorRegistrationRepository.delete(r);
            }
            return;
        }

        throw new GRSException(HttpStatus.BAD_REQUEST, "Instructor not teaching course.");
    }

    /**
     * GetInstructorRegistration: get the registration of an instructor for a session
     * @param sessionId: id of the session
     * @param email: email of the instructor
     * @return the registration of the instructor for the session
     * @throws GRSException instructor not found, Session not found, Instructor not registered for this session
     */
    @Transactional
    public InstructorRegistration getInstructorRegistrationByInstructorAndSession(int sessionId, String email) {
        if (sessionId == -1 || email == null) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "No instructor or session entered.");
        }

        Instructor instructor = instructorRepository.findInstructorByEmail(email);
        Session session = sessionRepository.findSessionById(sessionId);
        if (instructor == null)
            throw new GRSException(HttpStatus.NOT_FOUND, "Instructor not found.");
        if (session == null)
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");

        InstructorRegistration instructorRegistration = instructorRegistrationRepository
                .findInstructorRegistrationByInstructor_idAndSession_id(instructor.getId(), session.getId());
        if (instructorRegistration == null)
            throw new GRSException(HttpStatus.BAD_REQUEST, "Instructor not registered for this session.");
        return instructorRegistration;
    }

    /**
     * GetInstructorRegistrationsByInstructor: get all registrations for an instructor
     * @param email: email of the instructor
     * @return list of all instructor registrations for an instructor
     * @throws GRSException no instructor entered, Instructor not found, No registrations found in the system
     */
    @Transactional
    public List<InstructorRegistration> getInstructorRegistrationsByInstructor(String email) {
        if(email == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No instructor enterred.");
        }
        
        Instructor instructor = instructorRepository.findInstructorByEmail(email);
        if (instructor == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Instructor not found.");
        }
        List<InstructorRegistration> registrations = instructorRegistrationRepository.findInstructorRegistrationsByInstructor_Email(email);
        if (registrations.size() == 0){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No registrations found in the system.");
        }
        return registrations;
    }

    /**
     * GetInstructorRegistrationBySession: get all registrations for a session
     * @param sessionId: id of the session
     * @return list of all instructor registrations for a session
     * @throws GRSException no session entered, Session not found, No registrations found in the system
     */
    @Transactional
    public List<InstructorRegistration> getInstructorRegistrationBySession(int sessionId) {
        if(sessionId == 0){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No session entered.");
        }
        
        Session session = sessionRepository.findSessionById(sessionId);
        if (session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        }
        List<InstructorRegistration> registrations = instructorRegistrationRepository.findInstructorRegistrationsBySession_id(sessionId);
        if (registrations.size() == 0){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No registrations found in the system.");
        }
        return registrations;
    }

    /**
     * UpdateInstructorRegistration: updating the instructor registration
     * @param oldId : old id of instructor registration
     * @param instructorRegistrationDto: new instructor registration
     * @return updated instructor registration
     */
    @Transactional
    public InstructorRegistration updateInstructorRegistration(int oldId, InstructorRegistrationDto instructorRegistrationDto) {
        InstructorRegistration instructorRegistration = instructorRegistrationRepository.findInstructorRegistrationById(oldId);
        if (instructorRegistration == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Instructor Registration not found.");
        }
        instructorRegistration.setId(instructorRegistrationDto.getId());
        instructorRegistration.setInstructor(instructorRegistrationDto.getInstructor());
        instructorRegistration.setSession(instructorRegistrationDto.getSession());
        instructorRegistration.setDate(instructorRegistrationDto.getDate());
        return instructorRegistrationRepository.save(instructorRegistration);
    }
}
