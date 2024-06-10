package ca.mcgill.ecse321.gymregistration.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.dto.CustomerRegistrationDto;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.Session;;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerRegistrationServiceIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private CustomerRepository customerRepository;


    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        customerRegistrationRepository.deleteAll();
        customerRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    @Test
    public void testCreateCustomerRegistration(){
        CustomerRegistrationDto customerRegistrationDto = testCreateCustomerRegistration("customer@email.com");
    }

    public CustomerRegistrationDto testCreateCustomerRegistration(String email){
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setCreditCardNumber("1234 5678 1234 5678");
        customerRepository.save(customer);

        Session session = new Session();
        session.setCapacity(100);
        session.setDate(Date.valueOf(LocalDate.of(2025, 5, 1)));
        session.setStartTime(Time.valueOf(LocalTime.of(12, 0, 0)));
        session.setEndTime(Time.valueOf(LocalTime.of(13, 0, 0)));
        sessionRepository.save(session);

        CustomerRegistrationDto customerRegistrationDto = new CustomerRegistrationDto(0, null, session, customer);
        String url = "/customer-registrations/register";
        ResponseEntity<CustomerRegistrationDto> response = client.postForEntity(url, customerRegistrationDto, CustomerRegistrationDto.class);
        
        assertNotNull(response.getBody());
        assertEquals(email, response.getBody().getCustomer().getEmail());
        assertEquals(session.getId(), response.getBody().getSession().getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has correct status");
        return response.getBody();
    }



}
