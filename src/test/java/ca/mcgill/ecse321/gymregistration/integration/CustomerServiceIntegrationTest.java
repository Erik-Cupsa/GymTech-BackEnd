package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.dto.CustomerDto;
import ca.mcgill.ecse321.gymregistration.dto.InstructorDto;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Person;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerServiceIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase(){
        customerRepository.deleteAll();
    }

    @Test
    public void testCreateAndGetCustomer(){
        String email = testCreateCustomer("customer@email.com", "password");
        testGetCustomer(email);
    }

    @Test
    public void testCreateAndLogInCustomer(){
        String email = testCreateCustomer("customer@email.com", "password");
        testLogInCustomer(email, "password");
    }

    @Test
    public void testCreateCustomerAndUpdateCreditCard(){
        String email = testCreateCustomer("customer@email.com", "password");
        testUpdateCustomerCreditCard(email, "password", "1234 5678 1234 5678");
    }

    @Test
    public void testCreateCustomerAndUpdateEmail(){
        String email = testCreateCustomer("customer@email.com", "password");
        testUpdateCustomerEmail(email, "password", "customer@newEmail.com");
    }

    @Test
    public void testCreateCustomerAndUpdatePassword(){
        String email = testCreateCustomer("customer@email.com", "password");
        testUpdateCustomerPassword(email, "password", "newPassword");
    }

    @Test
    public void testCreateCustomerAndUpdateToInstructor(){
        String email = testCreateCustomer("customer@email.com", "password");
        testUpdateCustomerToInstructor(email);
    }

    @Test
    public void testCreateAndDeleteCustomer(){
        String email = testCreateCustomer("customer@email.com", "password");
        testDeleteCustomer(email);
    }


    @Test
    public void testCreateAndGetAllCustomers(){
        testCreateCustomer("customer1@email.com", "password1");
        testCreateCustomer("customer2@email.com", "password2");
        testGetAllCustomers(2);
    }

    private String testCreateCustomer(String email, String password){
        Person person = new Person();
        personRepository.save(person);
        ResponseEntity<CustomerDto> response = client.postForEntity("/customers/create", 
                new CustomerDto(email, password, person), CustomerDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");

        return response.getBody().getEmail();
    }

    private void testLogInCustomer(String email, String password){
        ResponseEntity<CustomerDto> response = client.getForEntity("/customers/login/"+email+"/"+password, CustomerDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertNotNull(response.getBody(), "Reponse has body");
        assertEquals(email, response.getBody().getEmail());
        assertEquals(password, response.getBody().getPassword());
    }

    private void testGetCustomer(String email){
        ResponseEntity<CustomerDto> response = client.getForEntity("/customers/"+email, CustomerDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertNotNull(response.getBody(), "Reponse has body");
        assertEquals(email, response.getBody().getEmail());
    }

    private void testGetAllCustomers(int size){
        ResponseEntity<List<CustomerDto>> response = client.exchange(
            "/customers",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<CustomerDto>>() {}
            );
        assertEquals(size, response.getBody().size());
        List<CustomerDto> responseCustomerDtos = response.getBody();
        for(CustomerDto c : responseCustomerDtos){
            assertNotNull(customerRepository.findCustomerByEmail(c.getEmail()));
        }
    }

    private void testUpdateCustomerCreditCard(String email, String password, String newCreditCard){
        CustomerDto customerDto = new CustomerDto(email, password, null);

        HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(customerDto, null);
        ResponseEntity<CustomerDto> response = client.exchange(
                "/customers/updateCreditCard/"+newCreditCard, 
                HttpMethod.PUT, 
                requestEntity, 
                CustomerDto.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertEquals(newCreditCard, response.getBody().getCreditCardNumber());
    }

    private void testUpdateCustomerEmail(String oldEmail, String password, String newEmail){
        CustomerDto customerDto = new CustomerDto(oldEmail, password, null);
        
        HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(customerDto, null);
        ResponseEntity<CustomerDto> response = client.exchange(
            "/customers/updateEmail/"+newEmail, 
            HttpMethod.PUT,
            requestEntity,
            CustomerDto.class);
        
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertEquals(newEmail, response.getBody().getEmail());
    }

    private void testUpdateCustomerPassword(String email, String oldPassword, String newPassword){
        CustomerDto customerDto = new CustomerDto(email, oldPassword, null);
        
        HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(customerDto, null);
        ResponseEntity<CustomerDto> response = client.exchange(
            "/customers/updatePassword/"+newPassword, 
            HttpMethod.PUT,
            requestEntity,
            CustomerDto.class);
        
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response has correct status");
        assertEquals(newPassword, response.getBody().getPassword());
    }

    private void testUpdateCustomerToInstructor(String email){
        Owner owner = new Owner();

        HttpEntity<Owner> requestEntity = new HttpEntity<>(owner, null);
        ResponseEntity<InstructorDto> response = client.exchange(
            "/customers/updateToInstructor/"+email, 
            HttpMethod.PUT,
            requestEntity,
            InstructorDto.class);
        
        assertNotNull(response.getBody());
        assertEquals(InstructorDto.class, response.getBody().getClass());
    }

    private void testDeleteCustomer(String email){
        String url = "/customers/delete/"+email;
        Owner owner = new Owner();

        HttpEntity<Owner> requestEntity = new HttpEntity<>(owner, null);
        ResponseEntity<String> response = client.exchange(
            url,
            HttpMethod.DELETE,
            requestEntity,
            String.class);
        assertEquals(email, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    
}
