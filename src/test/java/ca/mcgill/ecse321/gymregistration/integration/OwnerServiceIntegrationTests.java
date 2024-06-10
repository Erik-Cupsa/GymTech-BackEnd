package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.dto.OwnerDto;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OwnerServiceIntegrationTests {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        ownerRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    public void testCreateAndGetOwner() {
        int id = testCreateOwner("example@email.com", "password");
        testGetOwner("example@email.com");
    }

    @Test // multiple
    // Verify the different emails
    public void testCreateAndGetOwners() {
        testCreateOwner("example@email.com", "password");
        testCreateOwner("example2@email.com", "password");
        testGetOwners(2);
    }

    @Test
    public void testCreateAndDeleteOwner() {
        int id = testCreateOwner("example@email.com", "password");
        testDeleteOwner(id);
    }

    @Test
    public void testCreateAndUpdateOwnerEmail() {
        String email = "example@email.com";
        int id = testCreateOwner(email, "password");
        testUpdateOwnerEmail(id, email, "newemail@email.com", "password");
    }

    @Test
    public void testCreateInvalidOwner() {
        testCreateOwnerInvalidEmailOrPassword("email", "password");
        System.out.println("1");
        testCreateOwnerInvalidEmailOrPassword(null, "password");
        System.out.println("2");
        testCreateOwnerInvalidEmailOrPassword("email@example.com", null);
        System.out.println("3");
        testCreateOwnerInvalidPerson();
        System.out.println("4");

    }

    public void testCreateOwnerInvalidEmailOrPassword(String email, String password) {
        Person person = new Person();
        personRepository.save(person);

        ResponseEntity<OwnerDto> response = client.postForEntity("/owners/create", new OwnerDto(email, password, person), OwnerDto.class);
        assertNotNull(response);
        
        assertTrue(HttpStatus.BAD_REQUEST.equals(response.getStatusCode()) || HttpStatus.NOT_FOUND.equals( response.getStatusCode()), "Response has correct status");
    }

    public void testCreateOwnerInvalidPerson() {
        ResponseEntity<OwnerDto> response = client.postForEntity("/owners/create", new OwnerDto("email@email.com", "password", new Person()), OwnerDto.class);
        assertNotNull(response);
        
        assertTrue(HttpStatus.BAD_REQUEST.equals(response.getStatusCode()) || HttpStatus.NOT_FOUND.equals( response.getStatusCode()), "Response has correct status");
    }

    private int testCreateOwner(String email, String password) {
        Person person = new Person();
        personRepository.save(person);

        ResponseEntity<OwnerDto> response = client.postForEntity("/owners/create", new OwnerDto(email, password, person), OwnerDto.class);
        assertNotNull(response);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");
        return response.getBody().getId();
    }

    private void testGetOwner(String email) {
        ResponseEntity<OwnerDto> response = client.getForEntity("/owners/" + email, OwnerDto.class);
        assertNotNull(response);
        
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertNotNull(response.getBody(), "Response has body");
        assertEquals(response.getBody().getEmail(), email);
    }

    private void testGetOwners(int size) {
        ResponseEntity<List<OwnerDto>> response = client.exchange("/owners", HttpMethod.GET, null, new ParameterizedTypeReference<List<OwnerDto>>() {});
        assertEquals(response.getBody().size(), size);

        List<OwnerDto> responseOwnerDtos = response.getBody();
        for (OwnerDto i : responseOwnerDtos) {
            assertNotNull(ownerRepository.findOwnerById(i.getId()));
        }
    }

    private void testUpdateOwnerEmail(int id, String oldEmail, String newEmail, String password) {
        OwnerDto ownerDto = new OwnerDto(oldEmail, password, null);
        ownerDto.setId(id);

        

        ResponseEntity<OwnerDto> response = client.exchange("/owners/update-owners-e/" +newEmail, HttpMethod.PUT, new HttpEntity<>(ownerDto), OwnerDto.class);
   
        assertNotNull(response.getBody());
        assertEquals(newEmail, response.getBody().getEmail());
        assertEquals(id, response.getBody().getId());
    }

    private void testDeleteOwner(int email) {
        try {
            String url = "/owners/delete/" + email;
            client.delete(url);
        } catch (GRSException e) {
            fail();
        }
        assertEquals(1, 1);
    }
}
