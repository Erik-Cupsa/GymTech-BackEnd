package ca.mcgill.ecse321.gymregistration.integration;

import ca.mcgill.ecse321.gymregistration.dao.ClassTypeRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dto.ClassTypeDto;
import ca.mcgill.ecse321.gymregistration.dto.GymUserDto;
import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClassTypeServiceIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private ClassTypeRepository classTypeRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        classTypeRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    @Test
    public void testCreateAndGetClassTypeByName() {
        String name = "Yoga";
        ClassTypeDto createdClassType = testCreateClassType(name, true);
        testGetClassTypeByName(name, createdClassType.getId());
    }

   @Test
   public void testCreateAndApproveProposedClassType() {
       String name = "Yoga";
       ClassTypeDto createdClassType = testCreateClassType(name, true);
       System.out.println("moving on");
       testApproveProposedClassType(name, createdClassType.getId());
   }
//
   @Test
   public void testGetAllClassTypes() {
       testCreateClassType("Crossfit", true);
       testCreateClassType("Zumba", true);
       testGetAllClassTypes(2);
   }

   @Test
   public void testProposeAndApproveClassType()
   {
    int id = testProposeClassType("Yoga");
    System.out.println("Moving on");
    testApproveProposedClassType("Yoga", id);
    }

    @Test
    public void testCreateandUpdateClassType()
    {
        int id = testProposeClassType("Yoga");
        System.out.println("moving on update");
        testUpdateClassType("Example@email.com", id, "Goat Yoga", "Yoga");
    }
   

    public ClassTypeDto testCreateClassType(String name, boolean isApproved) {
        ClassType classType = new ClassType();
        Owner owner = ownerRepository.findOwnerByEmail("Example@email.com");
        if(owner==null)
        {
            owner = new Owner();
            owner.setEmail("Example@email.com");
            owner = ownerRepository.save(owner);
        }
        classType.setApproved(isApproved);
        classType.setName(name);
        String url = "/class-types/create/"+owner.getEmail();
        ResponseEntity<ClassTypeDto> response = client.postForEntity(url, new ClassTypeDto(classType), ClassTypeDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");
        return response.getBody();
    }

    private void testGetClassTypeByName(String name, int id) {
        ResponseEntity<ClassTypeDto> response = client.getForEntity("/class-types/" + name, ClassTypeDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
//        assertNotNull(response.getBody(), "Response has body");
//        assertEquals(name, response.getBody().getName());
//        assertEquals(id, response.getBody().getId());
    }
//
   private void testApproveProposedClassType(String name, int id) {
       Owner owner = ownerRepository.findOwnerByEmail("Example@email.com");
       GymUserDto gymUserDto = new GymUserDto(owner);
       String url = "/class-types/approve/" + name;
       ResponseEntity<ClassTypeDto> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(gymUserDto), ClassTypeDto.class);
       assertNotNull(response);
       assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
       assertNotNull(response.getBody(), "Response has body");
       assertEquals(name, response.getBody().getName());
       assertEquals(id, response.getBody().getId());
       assertTrue(response.getBody().isApproved());
   }
//
   private void testGetAllClassTypes(int size) {
       ResponseEntity<List<ClassTypeDto>> response = client.exchange("/class-types", HttpMethod.GET, null, new ParameterizedTypeReference<List<ClassTypeDto>>() {});
       assertNotNull(response);
       assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
       assertNotNull(response.getBody(), "Response has body");
       assertEquals(size, response.getBody().size());
   }

   public int testProposeClassType(String name) {
    // Mock data
   
    Owner owner = new Owner(); // Provide appropriate gym user object
    owner.setEmail("Example@email.com");
    ownerRepository.save(owner);
    // Make a POST request to the endpoint
    ResponseEntity<ClassTypeDto> response = client.postForEntity(
            "/class-types/propose/"+name,
            new GymUserDto(owner), // Assuming ClassTypeDto constructor accepts name and gymUser
            ClassTypeDto.class);

    // Verify response
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(name, response.getBody().getName());
    // Optionally, you can verify the returned ClassTypeDto content or other aspects of the response
    return response.getBody().getId();
}

public void testUpdateClassType(String email, int id, String newClassName, String oldName)
{
    ClassTypeDto classTypeDto= new ClassTypeDto(id,newClassName, true);

    String url = "/class-types/"+oldName+"/email/"+email;

    ResponseEntity<ClassTypeDto> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(classTypeDto), ClassTypeDto.class);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
    assertNotNull(response.getBody(), "Response has body");
    assertEquals(newClassName, response.getBody().getName());
    assertEquals(id, response.getBody().getId());
    assertTrue(response.getBody().isApproved());
}

}