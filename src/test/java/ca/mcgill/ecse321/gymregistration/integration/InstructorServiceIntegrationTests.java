package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.dto.InstructorDto;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstructorServiceIntegrationTests {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private InstructorRepository instructorRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        instructorRepository.deleteAll();
    }

    @Test
    public void testCreateAndGetInstructor() {
        int id = testCreateInstructor("example@email.com", "password");
        testGetinstructor("example@email.com");
    }

    @Test // multiple
    // Verify the different emails
    public void testCreateAndGetInstructors() {
        testCreateInstructor("example@email.com", "password");
        testCreateInstructor("example2@email.com", "password");
        testGetinstructors(2);
    }

    @Test
    public void testCreateAndDeleteInstructor() {
        int id = testCreateInstructor("example@email.com", "password");
        testDeleteInstructor(id);
    }

    @Test
    public void testCreateandUpdateInstructorEmail() {
        int id = testCreateInstructor("example@email.com", "password");
        testUpdateInstructorEmail(id,"example@email.com", "newemail@email.com","password");
    }

    @Test
    public void testCreateInvalidInstructor() {
        testCreateInstructorInvalidEmailOrPassword("email", "password");
        testCreateInstructorInvalidEmailOrPassword(null, "password");
        testCreateInstructorInvalidEmailOrPassword("email@exmaple.com", null);
        testCreateInstructorInvalidPerson();
    }

    @Test
    public void testcreateAndLogInInstructor()
    {
        int id = testCreateInstructor("example@email.com","password");
        testLogInInstructor("example@email.com", "password");
    }



    public void testCreateInstructorInvalidEmailOrPassword(String email, String password) {
        Person person = new Person();
        personRepository.save(person);

        ResponseEntity<InstructorDto> response = client.postForEntity("/instructors/create",
                new InstructorDto(email, password, person), InstructorDto.class);
        assertNotNull(response);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response has correct status");
    }

    public void testCreateInstructorInvalidPerson()
    {
        ResponseEntity<InstructorDto> response = client.postForEntity("/instructors/create",
                new InstructorDto("email@email.com", "password",  new Person()), InstructorDto.class);
        assertNotNull(response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Response has correct status");
    }

    private int testCreateInstructor(String email, String password) {
        Person person = new Person();
        personRepository.save(person);
        ResponseEntity<InstructorDto> response = client.postForEntity("/instructors/create",
                new InstructorDto(email, password, person), InstructorDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");

        return response.getBody().getId();
    }

    private void testGetinstructor(String email) {
        ResponseEntity<InstructorDto> response = client.getForEntity("/instructors/" + email, InstructorDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertNotNull(response.getBody(), "Response has body");
        assertEquals(response.getBody().getEmail(), email);
    }

    private void testGetinstructors(int size) {
        ResponseEntity<List<InstructorDto>> response = client.exchange(
                "/instructors",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InstructorDto>>() {
                });
        assertEquals(response.getBody().size(), size);
        List<InstructorDto> responseInstructorDtos = response.getBody();
        for (InstructorDto i : responseInstructorDtos) {
            assertNotNull(instructorRepository.findInstructorById(i.getId()));
        }
    }

    private void testUpdateInstructorEmail(int id, String email, String newEmail,String password ) {

        InstructorDto instructorDto = new InstructorDto(email, password, null);
        instructorDto.setId(id);

        HttpEntity<InstructorDto> requestEntity = new HttpEntity<>(instructorDto, null);

        ResponseEntity<InstructorDto> response = client.exchange(
                "/instructors/update-instructors-e/"+newEmail, // URL with path variables
                HttpMethod.PUT, // HTTP method
                requestEntity,
                InstructorDto.class);
        
        assertNotNull(response.getBody());
        assertEquals(newEmail, response.getBody().getEmail());
        assertEquals(id, response.getBody().getId());

    }

    private void testDeleteInstructor(int id) {
        try {
            String url = "/instructors/delete/" + id;
            client.delete(url);
        } catch (GRSException e) {
            fail();
        }
        assertEquals(1, 1);
    }

    public void testLogInInstructor(String email, String password)
    {
       String url= "/instructors/login/"+email+"/"+password;
       ResponseEntity<InstructorDto> response = client.getForEntity(url, InstructorDto.class);

       assertNotNull(response);
       assertNotNull(response.getBody());
       assertEquals(email, response.getBody().getEmail());
       assertEquals(password,response.getBody().getPassword());
       assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
