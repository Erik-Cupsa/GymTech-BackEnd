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
import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.InstructorRegistration;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.model.Session;

@SpringBootTest
public class InstructorRegistrationTests {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private ClassTypeRepository classTypeRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private InstructorRegistrationRepository instructorRegistrationRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        instructorRegistrationRepository.deleteAll();
        instructorRepository.deleteAll();
        personRepository.deleteAll();
        sessionRepository.deleteAll();
        classTypeRepository.deleteAll();
    }
    @Test
    public void testCreateAndReadInstructorRegistration() {
        // Create and persist person.
        Person maxime = createAndPersistPerson();

        // Create and persist instructor.
        Instructor instructorMaxime = createAndPersistInstructor(maxime);

        // Create and persist class type.
        ClassType badminton = createAndPersistClassType();

        // Create and persist session.
        Session badmintonSession = createAndPersistSession(badminton);

        // Create instructor registration.
        InstructorRegistration instructorRegistration = createInstructorRegistration(instructorMaxime, badmintonSession);

        // Save instructor registration to database.
        instructorRegistration = instructorRegistrationRepository.save(instructorRegistration);

        // Read instructor registration from database.
        InstructorRegistration instructorRegistrationFromDB = instructorRegistrationRepository.findInstructorRegistrationById(instructorRegistration.getId());

        // Assertions
        assertInstructorRegistrationAttributes(instructorRegistration, instructorRegistrationFromDB);
        assertInstructorAttributes(instructorMaxime, instructorRegistrationFromDB.getInstructor());
        assertPersonAttributes(maxime, instructorRegistrationFromDB.getInstructor().getPerson());
        assertSessionAttributes(badmintonSession, instructorRegistrationFromDB.getSession());
        assertClassTypeAttributes(badminton, instructorRegistrationFromDB.getSession().getClassType());
    }

    private Person createAndPersistPerson() {
        String name = "Maxime";
        Person person = new Person(name);
        return personRepository.save(person);
    }

    private Instructor createAndPersistInstructor(Person person) {
        String email = "emailaddress@emailprovider.us";
        String password = "instructorPassword";
        Instructor instructor = new Instructor(email, password, person);
        return instructorRepository.save(instructor);
    }

    private ClassType createAndPersistClassType() {
        String className = "Badminton";
        boolean isApproved = true;
        ClassType classType = new ClassType(className, isApproved);
        return classTypeRepository.save(classType);
    }

    private Session createAndPersistSession(ClassType classType) {
        Date sessionDate = Date.valueOf("2024-02-18");
        Time startTime = Time.valueOf("16:25:00");
        Time endTime = Time.valueOf("17:25:00");
        String sessionDescription = "Badminton for beginners";
        String sessionName = "Badminton 101";
        String sessionLocation = "Gymnasium";
        Session session = new Session(sessionDate, startTime, endTime, sessionDescription, sessionName, sessionLocation, classType);
        return sessionRepository.save(session);
    }

    private InstructorRegistration createInstructorRegistration(Instructor instructor, Session session) {
        Date registrationDate = Date.valueOf("2024-02-19");
        return new InstructorRegistration(registrationDate, instructor, session);
    }

    private void assertInstructorRegistrationAttributes(InstructorRegistration expected, InstructorRegistration actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDate(), actual.getDate());
    }

    private void assertInstructorAttributes(Instructor expected, Instructor actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    private void assertPersonAttributes(Person expected, Person actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
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
        assertNotNull(actual.getClassType());
        assertEquals(expected.getClassType().getId(), actual.getClassType().getId());
        assertEquals(expected.getClassType().getName(), actual.getClassType().getName());
        assertEquals(expected.getClassType().getIsApproved(), actual.getClassType().getIsApproved());
    }

    private void assertClassTypeAttributes(ClassType expected, ClassType actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getIsApproved(), actual.getIsApproved());
    }
}
