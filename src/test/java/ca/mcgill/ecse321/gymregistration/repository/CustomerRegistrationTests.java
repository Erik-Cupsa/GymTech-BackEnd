package ca.mcgill.ecse321.gymregistration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; 

import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.ClassTypeRepository;
import ca.mcgill.ecse321.gymregistration.dao.CustomerRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.CustomerRegistration;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.model.Session;

@SpringBootTest
public class CustomerRegistrationTests {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ClassTypeRepository classTypeRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        customerRegistrationRepository.deleteAll();
        customerRepository.deleteAll();
        personRepository.deleteAll();
        sessionRepository.deleteAll();
        classTypeRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadCustomerRegistration() {
        // Create and persist person.
        Person simon = createAndPersistPerson();
        // Create and persist customer.
        Customer customerSimon = createAndPersistCustomer(simon);
        // Create and persist class type.
        ClassType yoga = createAndPersistClassType();
        // Create and persist session.
        Session session = createAndPersistSession(yoga);
        // Create customer registration.
        CustomerRegistration customerRegistration = createCustomerRegistration(session, customerSimon);
        // Save customer registration to the database.
        customerRegistration = customerRegistrationRepository.save(customerRegistration);
        // Read customer registration from the database.
        CustomerRegistration customerRegistrationFromDB = customerRegistrationRepository.findCustomerRegistrationById(customerRegistration.getId());
        // Assertions
        assertCustomerRegistrationAttributes(customerRegistration, customerRegistrationFromDB);
        assertSessionAttributes(session, customerRegistrationFromDB.getSession());
        assertClassTypeAttributes(yoga, customerRegistrationFromDB.getSession().getClassType());
        assertCustomerAttributes(customerSimon, customerRegistrationFromDB.getCustomer());
        assertPersonAttributes(simon, customerRegistrationFromDB.getCustomer().getPerson());
    }

    private Person createAndPersistPerson() {
        String personName = "Simon";
        Person person = new Person(personName);
        return personRepository.save(person);
    }

    private Customer createAndPersistCustomer(Person person) {
        String email = "customeremailaddress@emailprovider.org";
        String password = "password";
        String creditCardNumber = "1234 5678 1234 5678";
        Customer customer = new Customer(email, password, person, creditCardNumber);
        return customerRepository.save(customer);
    }

    private ClassType createAndPersistClassType() {
        String className = "Yoga";
        boolean isApproved = true;
        ClassType classType = new ClassType(className, isApproved);
        return classTypeRepository.save(classType);
    }

    private Session createAndPersistSession(ClassType yoga) {
        Date sessionDate = Date.valueOf("2024-02-18");
        Time startTime = Time.valueOf("15:48:00");
        Time endTime = Time.valueOf("16:48:00");
        String sessionDescription = "A description of this session.";
        String sessionName = "Session name";
        String sessionLocation = "Where the session takes place.";
        Session session = new Session(sessionDate, startTime, endTime, sessionDescription, sessionName, sessionLocation, yoga);
        return sessionRepository.save(session);
    }

    private CustomerRegistration createCustomerRegistration(Session session, Customer customer) {
        Date registrationDate = Date.valueOf("2024-02-17");
        return new CustomerRegistration(registrationDate, session, customer);
    }

    private void assertCustomerRegistrationAttributes(CustomerRegistration expected, CustomerRegistration actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDate(), actual.getDate());
    }

    private void assertSessionAttributes(Session expected, Session actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getStartTime(), actual.getStartTime());
        assertEquals(expected.getEndTime(), actual.getEndTime());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getLocation(), actual.getLocation());
    }

    private void assertClassTypeAttributes(ClassType expected, ClassType actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getIsApproved(), actual.getIsApproved());
    }

    private void assertCustomerAttributes(Customer expected, Customer actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getCreditCardNumber(), actual.getCreditCardNumber());
    }

    private void assertPersonAttributes(Person expected, Person actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }
}
