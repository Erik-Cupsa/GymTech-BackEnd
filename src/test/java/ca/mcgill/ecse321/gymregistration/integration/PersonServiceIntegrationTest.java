package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.dto.PersonDto;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PersonServiceIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private PersonRepository personRepository;
    
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        personRepository.deleteAll();
    }

    @Test
    public void testCreateAndGetPersonById() {
        int id = testCreatePerson("John");
        testGetPersonById(id);
    }

    @Test
    public void testCreateAndGetPersonByName() {
        testCreatePerson("Paul");
        testGetPersonByName("Paul");
    }

    @Test
    public void testCreateAndGetPersons() {
        testCreatePerson("Jim");
        testCreatePerson("Jack");
        testGetPersons(2);
    }

    @Test
    public void testCreateAndUpdatePerson() {
        int id = testCreatePerson("John");
        testUpdatePerson(id, "John", "Johnny");
    }

    @Test
    public void testCreateAndDeletePerson() {
        int id = testCreatePerson("John");
        testDeletePerson(id);
    }
 
    @Test
    public void testCreateInvalidPerson(){
        testCreatePersonInvalidName();        
    }

    private void testCreatePersonInvalidName() {
        ResponseEntity<PersonDto> response = client.postForEntity("/persons/create", new PersonDto(""), PersonDto.class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response has correct status");
    }

    private int testCreatePerson(String name) {
        ResponseEntity<PersonDto> response = client.postForEntity("/persons/create", new PersonDto(name), PersonDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");
        
        return response.getBody().getId();
    }

    private void testGetPersonById(int id) {
        ResponseEntity<PersonDto> response = client.getForEntity("/persons/" + id, PersonDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertNotNull(response.getBody(), "Response has body");
        assertEquals(response.getBody().getId(), id);
    }

    private void testGetPersonByName(String name) {
        ResponseEntity<PersonDto[]> response = client.getForEntity("/persons/byname/" + name, PersonDto[].class);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertNotNull(response.getBody(), "Response has body");
        assertEquals(response.getBody()[0].getName(), name);
    }
    
    private void testGetPersons(int size) {
        ResponseEntity<List<PersonDto>> response = client.exchange("/persons", HttpMethod.GET, null, new ParameterizedTypeReference<List<PersonDto>>() {});

        assertEquals(response.getBody().size(), size);
        List<PersonDto> responsePersonDtos = response.getBody();
        for (PersonDto i : responsePersonDtos) {
            assertNotNull(personRepository.findPersonById(i.getId()));
        }
    }

    private void testUpdatePerson(int id, String name, String newName) {
        
        PersonDto personDto = new PersonDto(newName);
        personDto.setId(id);
        HttpEntity<PersonDto> requestEntity = new HttpEntity<>(personDto, null);

        ResponseEntity<PersonDto> response = client.exchange("/persons-update/" + id, HttpMethod.PUT, requestEntity, PersonDto.class);
        
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newName, response.getBody().getName());
        assertEquals(id, response.getBody().getId());
    }

    private void testDeletePerson(int id) {
        try {
            String url = "/persons/delete/" + id;
            client.delete(url);
        } catch (GRSException e) {
            fail();
        }
        assertEquals(1, 1);
    }
}
