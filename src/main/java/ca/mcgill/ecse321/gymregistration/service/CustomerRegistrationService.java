 /******************************************** 
    MANY OF THE METHODS IN THIS CLASS VIOLATE THE 20 LINES CONSTRAINT
    DUE TO THE SHEER NUMBER OF FIELDS THAT MUST BE SET IT WAS UNAVOIDABLE
    ***************************************************/
package ca.mcgill.ecse321.gymregistration.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.Date;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.CustomerRegistration;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Session;

import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import org.springframework.http.HttpStatus;

@Service
public class CustomerRegistrationService {
    
    @Autowired
    CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    InstructorRegistrationRepository instructorRegistrationRepository;
    @Autowired
    SessionRepository sessionRepository;

     /**
     * RegisterCustomerToSession: Create a new customer registration to add a customer to a session
     * @param sessionId: id of the session
     * @param email: email of the customer
     * @return new customer registration
     * @throws GRSException if attempt to register customer to session is invalid
     */
    @Transactional
    public CustomerRegistration registerCustomerToSession(int sessionId, String email){ //note: method cannot be reduced to 20 lines do to multiple error checks
        if (sessionId < 0 || email == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "No session or customer entered.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        } 
        if (customer.getCreditCardNumber() == null || customer.getCreditCardNumber().trim().isEmpty()){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Credit card must be entered to register for a class.");
        }
        Session session = sessionRepository.findSessionById(sessionId);
        if(session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        }
        CustomerRegistration customerRegistration = customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(customer, session);
        if (customerRegistration != null){
            throw new GRSException(HttpStatus.CONFLICT, "Cannot register for same session twice.");
        }
        List<CustomerRegistration> customerRegistrations = customerRegistrationRepository.findCustomerRegistrationsBySession_Id(sessionId);
        if(customerRegistrations.size() == session.getCapacity()){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Session is already at capacity.");
        }
        //getting current date and time, converting startTime and date to LocalDateTime, and calculating difference
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime sessionStartDateTime = LocalDateTime.of(session.getDate().toLocalDate(), session.getStartTime().toLocalTime());
        LocalDateTime sessionEndDateTime = LocalDateTime.of(session.getDate().toLocalDate(), session.getEndTime().toLocalTime());
        if (sessionEndDateTime.isBefore(currentDateTime)){         //checking if end of session was before the registration time (current date-time) 
            throw new GRSException(HttpStatus.BAD_REQUEST, "Cannot register for completed session.");
        }else if (sessionStartDateTime.isBefore(currentDateTime) && sessionEndDateTime.isAfter(currentDateTime)){         //checking if the registration (current date-time) is during the session
            throw new GRSException(HttpStatus.BAD_REQUEST, "Cannot register for in-progress session.");
        }
        customerRegistration = new CustomerRegistration(Date.valueOf(currentDateTime.toLocalDate()), session, customer);     
        customerRegistrationRepository.save(customerRegistration);
        return customerRegistration;
    }

    /**
     * getCustomerRegistrationByCustomerAndSession: fetch an existing registration associated to a customer and a session
     * @param sessionId: id of the session
     * @param email: email of the customer
     * @return registration of customer for a session
     * @throws GRSException if attempt to find registration is invalid
     */
    @Transactional
    public CustomerRegistration getCustomerRegistrationByCustomerAndSession(int sessionId, String email){
        if (sessionId < 0 || email == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No session or customer entered.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        } 
        Session session = sessionRepository.findSessionById(sessionId);
        if(session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        }
        CustomerRegistration customerRegistration = customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(customer, session);
        if (customerRegistration == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "The registration does not exist.");
        }
        return customerRegistration;
    }

    /**
     * UpdateCustomerRegistration: updating a customer registration with new session
     * @param oldSessionId: old session
     * @param newSessionId: new session
     * @param email: email of customer
     * @return Updated customer registration
     */
    @Transactional
    public CustomerRegistration updateCustomerRegistration(int oldSessionId, int newSessionId, String email) {
        if (email == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No session or customer entered.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        Session toUpdate = sessionRepository.findSessionById(oldSessionId);
        if(toUpdate == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Old session not found.");
        }
        Session session = sessionRepository.findSessionById(newSessionId);
        CustomerRegistration customerRegistration = customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(customer, session);
        if (customerRegistration == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "The registration does not exist.");
        }
        CustomerRegistration updated = new CustomerRegistration(Date.valueOf(LocalDateTime.now().toLocalDate()), session, customerRepository.findCustomerByEmail(email));
        customerRegistrationRepository.save(updated);
        return customerRegistration;
    }

    /**
     * GetCustomerRegistrationsByCustomer: gets all registrations for a customer
     * @param email: email of the customer to search with
     * @return list of all customer registrations for a customer
     * @throws GRSException if attempt to get all registrations from customer is invalid
     */
    @Transactional
    public List<CustomerRegistration> getCustomerRegistrationsByCustomer(String email){
        if (email == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No customer entered.");
        }

        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        } 
        List<CustomerRegistration> registrations = customerRegistrationRepository.findCustomerRegistrationsByCustomer_Email(email);
        if(registrations.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No registrations found in the system.");
        }
        return registrations;
    }

    /**
     * GetCustomerRegistrationsBySession: gets all registrations for a session
     * @param sessionId: id of the session to search with
     * @return list of all customer registrations for a session
     * @throws GRSException if attempt to get all registrations from session is invalid
     */
    @Transactional
    public List<CustomerRegistration> getCustomerRegistrationsBySession(int sessionId){
        if (sessionId < 0){
            throw new GRSException(HttpStatus.BAD_REQUEST, "No customer entered.");
        }

        Session session = sessionRepository.findSessionById(sessionId);
        if (session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        } 
        List<CustomerRegistration> registrations = customerRegistrationRepository.findCustomerRegistrationsBySession_Id(sessionId);
        if(registrations.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No registrations found in the system.");
        }
        return registrations;
    }

    /**
     * RemoveCustomerFromSession: remove customer from a session 
     * @param sessionId: id of the session
     * @param email: email of the customer
     * @param gymUser: gymUser trying to remove customer from session
     * @throws GRSException if attempt to remove customer from session is invalid
     */
    @Transactional
    public void removeCustomerFromSession(int sessionId, String email, GymUser gymUser) { //note: can not reduce to 20 lines do to multiple error checks
        if (sessionId < 0 || email == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "No session or customer entered.");
        }
        if(!(gymUser instanceof Owner)&&(gymUser instanceof Customer && !gymUser.getEmail().equals(email)) || (gymUser instanceof Instructor && instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(gymUser.getId(), sessionId) == null)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Customers can only be removed from sessions by the owner, instructor or themselves.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);
        Session session = sessionRepository.findSessionById(sessionId);
        if (customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        } 
        if(session == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Session not found.");
        }
        CustomerRegistration customerRegistration = customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(customer, session);
        if (customerRegistration == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "The registration does not exist.");
        }
        // current and session date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime sessionDateTime = LocalDateTime.of(session.getDate().toLocalDate(), session.getStartTime().toLocalTime());
        Duration timeDifference = Duration.between(currentDateTime, sessionDateTime);// getting difference
        if (timeDifference.toHours() < 48){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Cancellation of registration must be at least 48 hours before session start.");
        }
        customerRegistrationRepository.delete(customerRegistration);
    }

    /**
     * RemoveCustomerFromAllSessions: remove a customer from all sessions in which he is registered
     * @param email: email of the customer
     * @param gymUser: gymUser trying to remove customer from all sessions
     * @throws GRSException if attempt to remove customer from all sessions is invalid
     */
    @Transactional 
    public void removeCustomerFromAllSessions(String email, GymUser gymUser) {
        if( gymUser instanceof Customer && !gymUser.getEmail().equals(email)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Customers can only remove themselves from sessions.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);

        if (customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        customerRegistrationRepository.deleteCustomerRegistrationsByCustomer_Email(email);
    }
}
