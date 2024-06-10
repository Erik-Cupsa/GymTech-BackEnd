package ca.mcgill.ecse321.gymregistration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TestOwnerService {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OwnerService ownerService = new OwnerService();

    private Owner OWNER = new Owner();

    private int ID = OWNER.getId();

    private Person PERSON = new Person();

    private int PERSONID = PERSON.getId();

    String EMAIL = "example@email.com";
    String PASSWORD = "password";

    String INSTRUCTOREMAIL = "Instructorexample@email.com";

    String CUSTOMEREMAIL = "Instructorexample@email.com";

    @BeforeEach
    public void setMockOutput() {
        lenient().when(ownerRepository.findOwnerByEmailAndPassword(anyString(), anyString()))
                .thenAnswer((InvocationOnMock invocation) -> {
                    String email = invocation.getArgument(0);
                    String password = invocation.getArgument(1);
                    if (EMAIL.equals(email) && PASSWORD.equals(password)) {
                        Owner owner = new Owner();
                        owner.setId(ID);
                        owner.setEmail(EMAIL);
                        owner.setPassword(PASSWORD);
                        return owner;
                    } else {
                        return null;
                    }
                });

        lenient().when(ownerRepository.findOwnerByEmail(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            String email = invocation.getArgument(0);

            if (EMAIL.equals(email)) {
                Owner owner = new Owner();
                owner.setId(ID);
                owner.setEmail(EMAIL);
                return owner;
            } else {
                return null;
            }
        });

        lenient().when(personRepository.findPersonById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);

            if (id == PERSONID) {
                Person person = new Person();
                person.setId(ID);
                return person;
            } else {
                return null;
            }
        });

        lenient().when(instructorRepository.findInstructorByEmail(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            String email = invocation.getArgument(0);

            if (INSTRUCTOREMAIL.equals(email)) {
                Instructor instructor = new Instructor();
                instructor.setId(ID);
                instructor.setEmail(EMAIL);
                return instructor;
            } else {
                return null;
            }
        });

        lenient().when(customerRepository.findCustomerByEmail(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            String email = invocation.getArgument(0);

            if (INSTRUCTOREMAIL.equals(email)) {
                Customer customer = new Customer();
                customer.setId(ID);
                customer.setEmail(EMAIL);
                return customer;
            } else {
                return null;
            }
        });


        lenient().when(ownerRepository.save(any(Owner.class))).thenAnswer((InvocationOnMock invocation) -> {
            return invocation.getArgument(0); // Return the saved object
        });
        lenient().when(personRepository.save(any(Person.class))).thenAnswer((InvocationOnMock invocation) -> {
            return invocation.getArgument(0); // Return the saved object
        });
    }

    @Test
    public void testCreateOwner() {
        String email = "Email@email.com";
        String password = "password";
        Person person = new Person();
        int person_id = person.getId();
        Owner owner = new Owner();
        personRepository.save(person);
        lenient().when(ownerRepository.save(any(Owner.class))).thenAnswer((InvocationOnMock invocation) -> {
            return invocation.getArgument(0); // Return the saved object
        });
        
        owner = ownerService.createOwner(email, password, person_id);
        
        assertNotNull(owner);
        assertEquals(email, owner.getEmail());
        assertEquals(password, owner.getPassword());
    }

    @Test
    public void testUpdateEmail() {
        String email = "example@email.com";
        String password = "password";
        String newEmail = "NewEmail@email.com";
        Owner owner = new Owner();
        owner.setEmail(email);
        owner.setPassword(password);
        ownerRepository.save(owner);
        owner = ownerService.updateEmail(email, password, newEmail);
        assertEquals(newEmail, owner.getEmail());
    }

    @Test
    public void testUpdateInvalidEmail()
    {
        String email = "example@email.com";
        String password = "password";
        Owner owner = new Owner();
        owner.setEmail(email);
        ownerRepository.save(owner);
        try{
            ownerService.updateEmail(email, password, null);
        }
        catch(GRSException e)
        {
            assertEquals("Invalid new email.", e.getMessage());
        }
    }

    @Test
    public void testUpdatePassword()
    {
        String newPassword =  "new password";
        Owner owner = new Owner();
        owner.setEmail(EMAIL);
        owner.setPassword(PASSWORD);
        ownerRepository.save(owner);
        owner = ownerService.updatePassword(EMAIL, PASSWORD, newPassword);
        assertEquals(newPassword, owner.getPassword());
    }

    @Test
    public void testUpdateInvalidPassword()
    {
        
        Owner owner = new Owner();
        owner.setEmail(EMAIL);
        owner.setPassword(PASSWORD);
        ownerRepository.save(owner);
        try{
        owner = ownerService.updatePassword(EMAIL, PASSWORD, null);
        }
        catch(GRSException e)
        {
        assertEquals("Invalid new password.", e.getMessage());
        }        
    }

    @Test
    public void testUpdateNonExistantOwner()
    {
        Owner owner = new Owner();
        owner.setEmail(EMAIL);
        owner.setPassword(PASSWORD);
        ownerRepository.save(owner);
        try{
            owner = ownerService.updatePassword(EMAIL, PASSWORD+" ", PASSWORD);
            }
            catch(GRSException e)
            {
            assertEquals("Owner not found, invalid email and password combination.", e.getMessage());
            }
    }

    @Test
    public void testGetOwnerByEmail()
    {
        Owner owner = new Owner();
        owner.setEmail(EMAIL);
        ownerRepository.save(owner);
        Owner newOwner = ownerService.getOwnerByEmail(owner.getEmail());

        assertEquals(owner.getEmail(), newOwner.getEmail());
    }

    @Test
    public void testGetOwnerByInvalidEmail()
    {
        Owner owner = new Owner();
        owner.setEmail(EMAIL);
        ownerRepository.save(owner);
        try{
        Owner newOwner = ownerService.getOwnerByEmail(owner.getEmail()+" ");}
        catch(GRSException e)
        {
            assertEquals("Owner not found.", e.getMessage());
        }   
    }

    @Test
    public void testDeleteOwner() {
        lenient().doAnswer(invocation -> {
            Owner owner = invocation.getArgument(0);
            return null; // Simulate successful deletion
        }).when(ownerRepository).delete(any(Owner.class));
        Owner owner = new Owner(EMAIL, PASSWORD, new Person());
        ownerRepository.save(owner);
        ownerService.deleteOwner(owner.getEmail(), owner);
        lenient().doReturn(null).when(ownerRepository).findOwnerByEmail(owner.getEmail());
        owner = ownerRepository.findOwnerByEmail(owner.getEmail());
        assertNull(owner);
    }

    @Test
    public void testLogInOwner()
    {
        Owner owner = new Owner(EMAIL, PASSWORD, null);
        Owner logInowner;
        ownerRepository.save(owner);

        logInowner = ownerRepository.findOwnerByEmailAndPassword(EMAIL, PASSWORD);

        assertNotNull(logInowner);
        assertEquals(EMAIL, logInowner.getEmail());
        assertEquals(PASSWORD, logInowner.getPassword());
    }

    @Test
    public void testLogInOwnerInvalidEmail()
    {
        Owner owner = new Owner(EMAIL, PASSWORD, null);
        Owner logInowner;
        ownerRepository.save(owner);
        try{
        logInowner = ownerRepository.findOwnerByEmailAndPassword(EMAIL+" 1", PASSWORD);}
        catch(GRSException e)
        {
            assertEquals("Invalid email or password.", e.getMessage());
        }
    }

    @Test
    public void testLogInOwnerInvalidPassword()
    {
        Owner owner = new Owner(EMAIL, PASSWORD, null);
        Owner logInowner;
        ownerRepository.save(owner);
        try{
        logInowner = ownerRepository.findOwnerByEmailAndPassword(EMAIL, PASSWORD+"1");}
        catch(GRSException e)
        {
            assertEquals("Invalid email or password.", e.getMessage());
        }
    }
}
