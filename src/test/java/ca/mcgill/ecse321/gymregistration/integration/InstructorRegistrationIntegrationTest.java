package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.dto.InstructorRegistrationDto;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.InstructorRegistration;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Session;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstructorRegistrationIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private InstructorRegistrationRepository instructorRegistrationRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        instructorRegistrationRepository.deleteAll();
        instructorRepository.deleteAll();
        sessionRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    public void testCreateandGetInstructorRegistration() {
        int id = testCreateInstructorRegistration("example@Email.com");
        testgetInstructorRegistration(id);
    }

    @Test
    public void testCreateandDeleteInstructorRegistrationOneInstructor() {
        int id = testCreateInstructorRegistration("example@Email.com");
        testDeleteInstructorRegistration(id, 1, id);
    }

    @Test
    public void testCreateandDeleteInstructorRegistrationTwoInstructors() {
        int id = testCreateInstructorRegistration("example@Email.com");
        id = testCreateInstructorRegistration("example1@Email.com");
        testDeleteInstructorRegistration(id, 2, id);
    }

    @Test
    public void testCreateandDeleteInstructorRegistrationTwoInstructorsWithoutPermission() {
        int id = testCreateInstructorRegistration("example@Email.com");
        testCreateInstructorRegistration("example1@Email.com");
        testDeleteInstructorRegistration(id, 2, 1000);
    }

    @Test
    public void testCreateandDeleteInstructorRegistrationTwoInstructorsAsOwner() {
        int id = testCreateInstructorRegistration("example@Email.com");
        testCreateInstructorRegistration("example1@Email.com");
        Owner owner = new Owner();
        ownerRepository.save(owner);
        testDeleteInstructorRegistration(id, 2, owner.getId());
    }

    @Test 
    public void testCreateInvalidInstructorRegistrationBadEmail()
    {
        try{
            testCreateInstructorRegistration("fakeEmail");
        }
        catch(GRSException e)
        {
            assertEquals("Instructor not found.", e.getMessage());
        }
    }

    @Test
    public void testCreateInvalidInstructorRegistrationBadSession()
    {
        Instructor instructor = new Instructor();
        Session session = new Session();
        instructor.setEmail("example@Email.com");
        instructorRepository.save(instructor);
        sessionRepository.save(session);
        InstructorRegistrationDto instructorRegistrationDto = new InstructorRegistrationDto(null, instructor, new Session());
        String url = "/instructor-registration/create";
        ResponseEntity<InstructorRegistrationDto> response = client.postForEntity(url, instructorRegistrationDto,
                InstructorRegistrationDto.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "response has correct status"); 
    }

    @Test
    public void testCreateandGetInstructorRegistrationBySessionId()
    {
        int id = testCreateInstructorRegistration("example@Email.com");

        testgetInstructorRegistrationsBySessionId(id);
    }

    @Test
    public void testCreateandGetInstructorRegistrationByEmail()
    {
        int id = testCreateInstructorRegistration("example@Email.com");

        testgetInstructorRegistrationsByEmail(id);
    }


    public int testCreateInstructorRegistration(String email) {
        Instructor instructor = new Instructor();
        Session session = new Session();
        instructor.setEmail(email);
        instructorRepository.save(instructor);
        sessionRepository.save(session);
        InstructorRegistrationDto instructorRegistrationDto = new InstructorRegistrationDto(null, instructor, session);
        String url = "/instructor-registration/create";
        ResponseEntity<InstructorRegistrationDto> response = client.postForEntity(url, instructorRegistrationDto,
                InstructorRegistrationDto.class);
        assertNotNull(response.getBody());
        assertEquals(instructor.getId(), response.getBody().getInstructor().getId());
        assertEquals(session.getId(), response.getBody().getSession().getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");
        return response.getBody().getId();
    }

    public void testgetInstructorRegistration(int id) {
        InstructorRegistration instructorRegistration = instructorRegistrationRepository.findInstructorRegistrationById(id);
        String url = "/instructor-registration/" + instructorRegistration.getSession().getId() + "/" + instructorRegistration.getInstructor().getEmail();
        ResponseEntity<InstructorRegistrationDto> response = client.getForEntity(url,
                InstructorRegistrationDto.class);
        assertNotNull(response);
        assertEquals(id, response.getBody().getId());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
    }

    public void testgetInstructorRegistrationsBySessionId(int id)
    {
        InstructorRegistration instructorRegistration = instructorRegistrationRepository.findInstructorRegistrationById(id);
        String url = "/instructor-registration-s/" + instructorRegistration.getSession().getId();

        ResponseEntity<List<InstructorRegistration>> response = client.exchange(url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<InstructorRegistration>>() {});

        assertNotNull(response);
        assertEquals(response.getBody().size(),1);        
    }

    public void testgetInstructorRegistrationsByEmail(int id)
    {
        InstructorRegistration instructorRegistration = instructorRegistrationRepository.findInstructorRegistrationById(id);
        String url = "/instructor-registration-i/" + instructorRegistration.getInstructor().getEmail();

        ResponseEntity<List<InstructorRegistration>> response = client.exchange(url,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<InstructorRegistration>>() {});

        assertNotNull(response);
        assertEquals(response.getBody().size(),1);
    }

    public void testDeleteInstructorRegistration(int id, int numInstructors, int gid) {

        String url = "/instructor-registration/delete/" + id + "/" + gid;
        try {
            client.delete(url);
            assertEquals(1, 1);// if no errors then return sucessful
            return;
        } catch (GRSException e) {
            if (numInstructors == 1) {
                assertEquals("not enough instructors registered", e.getMessage());
                return;
            } else {
                assertEquals("You don't have permission to remove this instructor", e.getMessage());
                return;
            }
        }
    }

}
