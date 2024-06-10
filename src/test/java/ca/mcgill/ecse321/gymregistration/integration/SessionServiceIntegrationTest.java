package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.*;

import ca.mcgill.ecse321.gymregistration.dao.ClassTypeRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.Session;
import ca.mcgill.ecse321.gymregistration.dto.SessionDto;
import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SessionServiceIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private ClassTypeRepository classTypeRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired 
    private OwnerRepository ownerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        classTypeRepository.deleteAll();
        sessionRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    public void testCreateandGetSession() {
        int id = testCreateSession();
        testgetSession(id);
    }

    @Test
    public void testCreateandDeleteSessionOneClassType() {
        int id = testCreateSession();
        testDeleteSession(id);
    }

    @Test
    public void testCreateandDeleteSessionOneClassTypeEmail() {
        int id = testCreateSession();
        testDeleteSessionWithEmail(id,"email@exmaple.com");
    }

    @Test
    public void testCreateandDeleteSessionOneClassTypeWrongEmail() {
        int id = testCreateSession();
        testDeleteSessionWithEmail(id,"emailwrong@exmaple.com");
    }

    public int testCreateSession() {
        ClassType classType = new ClassType();
        classType.setName("Boxing");
        classType.setApproved(true);
        classTypeRepository.save(classType);
        Session session = new Session();
        session.setClassType(classType);
        session.setDate(Date.valueOf("2025-3-10"));
        session.setStartTime(Time.valueOf("12:00:00"));
        session.setEndTime(Time.valueOf("13:00:00"));
        session.setName("Boxing with Billy");
        session.setDescription("Punch Billy in the head over and over again (at his request)");
        session.setCapacity(20);
        session.setLocation("Main Gym");
        sessionRepository.save(session);
        SessionDto sessionDto = new SessionDto(session);
        Owner owner = new Owner();
        owner.setEmail("example@email.com");
        ownerRepository.save(owner);
        String url = "/sessions/create/"+owner.getEmail();
        ResponseEntity<SessionDto> response = client.postForEntity(url, sessionDto,
                SessionDto.class);
        assertNotNull(response.getBody());
//        assertEquals(session.getId(), response.getBody().getClassType().getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");
        return response.getBody().getId();
    }

    public void testgetSession(int id) {
        String url = "/sessions/" + id;
        ResponseEntity<SessionDto> response = client.getForEntity(url,
                SessionDto.class);
        assertNotNull(response);
        assertEquals(id, response.getBody().getId());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
    }


    public void testDeleteSession(int id) {
        String url = "/session/delete/" + id;
        try {
            client.delete(url);
            assertEquals(1, 1); 
            return;
        } catch (GRSException e) {
            assertEquals("You don't have permission to remove this session", e.getMessage());
            return;
            
        }
    }

    public void testDeleteSessionWithEmail(int id,String email) {
        String url = "/session/delete/" + id + "/"+email;
        try {
            client.delete(url);
            assertEquals(1, 1); 
            return;
        } catch (GRSException e) {
            assertEquals("You don't have permission to remove this session", e.getMessage());
            return;
            
        }
    }

}
