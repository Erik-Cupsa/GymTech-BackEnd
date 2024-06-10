package ca.mcgill.ecse321.gymregistration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.ClassTypeRepository;
import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.Session;

@SpringBootTest
public class SessionTests {
    @Autowired
    private ClassTypeRepository classTypeRepository;
    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        sessionRepository.deleteAll();
        classTypeRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadSession() {
        // Create and persist class type.
        ClassType classType = createAndPersistClassType("testClassType" + System.currentTimeMillis(), true);

        // Create session.
        Session session = createAndPersistSession("2024-02-17", "17:22:00", "18:22:00",
                "A description of the session.", "Session name", "Where session takes place.", classType);

        // Read session from database.
        Session sessionFromDB = sessionRepository.findSessionById(session.getId());

        // Assertions
        assertSessionAttributes(session, sessionFromDB);
        assertClassTypeAttributes(classType, sessionFromDB.getClassType());
    }

    @Test
    public void testFindSessionsByClassTypeName() {
        // Create and persist class type.
        ClassType classType = createAndPersistClassType("testClassType", true);

        // Create sessions for the class type.
        createAndPersistSession("2024-02-17", "17:22:00", "18:22:00",
                "A description of the session 1", "Session name 1", "Where session takes place 1", classType);

        createAndPersistSession("2024-02-18", "19:00:00", "20:00:00",
                "A description of the session 2", "Session name 2", "Where session takes place 2", classType);

        // Perform the test
        List<Session> sessions = sessionRepository.findSessionsByClassType_Name(classType.getName());

        // Assertions
        assertSessionsListAttributes(sessions);
    }

    private ClassType createAndPersistClassType(String name, boolean isApproved) {
        ClassType classType = new ClassType(name, isApproved);
        return classTypeRepository.save(classType);
    }

    private Session createAndPersistSession(String date, String startTime, String endTime,
                                            String description, String sessionName, String location, ClassType classType) {
        Session session = new Session(Date.valueOf(date), Time.valueOf(startTime), Time.valueOf(endTime),
                description, sessionName, location, classType);
        return sessionRepository.save(session);
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

    private void assertSessionsListAttributes(List<Session> sessions) {
        assertNotNull(sessions);
        assertEquals(2, sessions.size());

        assertEquals("A description of the session 1", sessions.get(0).getDescription());
        assertEquals("Session name 1", sessions.get(0).getName());
        assertEquals("Where session takes place 1", sessions.get(0).getLocation());

        assertEquals("A description of the session 2", sessions.get(1).getDescription());
        assertEquals("Session name 2", sessions.get(1).getName());
        assertEquals("Where session takes place 2", sessions.get(1).getLocation());
    }
}
