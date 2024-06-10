package ca.mcgill.ecse321.gymregistration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@ExtendWith(MockitoExtension.class)
public class TestCustomerService {
    

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock 
    OwnerRepository ownerRepository;
    @Mock
    InstructorRepository instructorRepository;
    @Mock
    GymUser gymUser;

    @InjectMocks
    private CustomerService customerService;

    private final Customer CUSTOMER = new Customer();
    private static final String EMAIL = "customer@email.com";
    private static final String PASSWORD = "password";
    private static final String CREDIT = "1234 5678 1234 5678";

    private final Person PERSON = new Person();
    private static final String PERSON_NAME = "George Bush";
    private static final int PERSON_ID = 0;


    @BeforeEach
    public void setUpMock(){
        lenient().when(customerRepository.findCustomerByEmail(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            if(invocation.getArgument(0).equals(EMAIL)){
                Customer customer = new Customer();
                customer.setEmail(EMAIL);
                customer.setPassword(PASSWORD);
                customer.setCreditCardNumber(CREDIT);
                customer.setPerson(PERSON);
                return customer;
            }else{
                return null;
            }
        });

        lenient().when(customerRepository.findCustomerByEmailAndPassword(anyString(), anyString())).thenAnswer((InvocationOnMock invocation) -> {
            String email = invocation.getArgument(0);
            String password = invocation.getArgument(1);
            if(email.equals(EMAIL) && password.equals(PASSWORD)){
                Customer customer = new Customer();
                customer.setEmail(EMAIL);
                customer.setPassword(password);
                customer.setCreditCardNumber(CREDIT);
                return customer;
            }else{
                return null;
            }
        });

        lenient().when(customerRepository.findAll()).thenAnswer((InvocationOnMock invocation) -> {
            CUSTOMER.setEmail(EMAIL);
            CUSTOMER.setPassword(PASSWORD);
            CUSTOMER.setCreditCardNumber(CREDIT);

            List<Customer> customersList = new ArrayList<>();
            customersList.add(CUSTOMER);
            return customersList;
        });

        //lenient().when(customerRepository.deleteCustomerByEmail(anyString()).thenReturn(null);

        lenient().when(personRepository.findPersonById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int personId = invocation.getArgument(0);
            if (personId == PERSON_ID){
                Person person = new Person();
                person.setName(PERSON_NAME);
                person.setId(PERSON_ID);
                return person;
            }else{
                return null;
            }
        });
    }

    @Test
    public void createCustomerTest(){
        String email = "customer@email.ca";
        String password = "testPassword";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customer);
        assertEquals(email, customer.getEmail());
        assertEquals(password, customer.getPassword());
    }

    @Test
    public void createCustomerNullEmail(){
        String email = null;
        String password = "testPassword";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
            fail();
        }catch(GRSException e){
            assertEquals("Must include valid email and password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void createCustomerEmptyEmail(){
        String email = "";
        String password = "testPassword";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
            fail();
        }catch(GRSException e){
            assertEquals("Must include valid email and password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void createCustomerInvalidEmail(){
        String email = "customerEmail";
        String password = "testPassword";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
            fail();
        }catch(GRSException e){
            assertEquals("Must include valid email and password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void createCustomerNullPassword(){
        String email = "customer@email.ca";
        String password = null;
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
            fail();
        }catch(GRSException e){
            assertEquals("Must include valid email and password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void createCustomerEmptyPassword(){
        String email = "customer@email.ca";
        String password = "";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
            fail();
        }catch(GRSException e){
            assertEquals("Must include valid email and password.", e.getMessage());
        }
        assertNull(customer);
    }

    /*
     * Email changed from new customer@email.ca -> in use customer@email.com
     */
    @Test
    public void createCustomerEmailAlreadyExists(){
        String email = "customer@email.com";
        String password = "testPassword";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, PERSON_ID);
            fail();
        }catch(GRSException e){
            assertEquals("User with email already exists.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void createCustomerPersonNotInSystem(){
        String email = "customer@email.ca";
        String password = "testPassword";
        

        Customer customer = null;

        try{
            customer = customerService.createCustomer(email, password, 1);
            fail();
        }catch(GRSException e){
            assertEquals("Person not found.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateCredit(){
        String newCreditCardNumber = "8765 4321 8765 4321";

        Customer customer = null;
        try{
            customer = customerService.updateCreditCard(EMAIL, PASSWORD, newCreditCardNumber);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customer);
        assertEquals(newCreditCardNumber, customer.getCreditCardNumber());
    }

    @Test
    public void testUpdateCreditSameNumber(){

        Customer customer = null;
        try{
            customer = customerService.updateCreditCard(EMAIL, PASSWORD, CREDIT);
            fail();
        }catch(GRSException e){
            assertEquals("New credit card number is same as old one.", e.getMessage());
        }
        assertNull(customer);
    }

    /*
     * Wrong email and password
     */
    @Test
    public void testUpdateCreditCustomerNotFound(){
        String email = "Testemail";
        String password = "Testpassword";
        String newCreditCardNumber = "8765 4321 8765 4321";

        Customer customer = null;
        try{
            customer = customerService.updateCreditCard(email, password, newCreditCardNumber);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateCreditCustomerWrongPassword(){
        String password = "Testpassword";
        String newCreditCardNumber = "8765 4321 8765 4321";

        Customer customer = null;
        try{
            customer = customerService.updateCreditCard(EMAIL, password, newCreditCardNumber);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateCreditCustomerWrongEmail(){
        String email = "testEmail";
        String newCreditCardNumber = "8765 4321 8765 4321";

        Customer customer = null;
        try{
            customer = customerService.updateCreditCard(email, PASSWORD, newCreditCardNumber);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateEmail(){
        String newEmail = "customer@gmail.com";

        Customer customer = null;
        try{
            customer = customerService.updateEmail(EMAIL, PASSWORD, newEmail);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customer);
        assertEquals(newEmail, customer.getEmail());
    }

    @Test
    public void testUpdateEmailToSame(){
        String newEmail = "customer@email.com";

        Customer customer = null;
        try{
            customer = customerService.updateEmail(EMAIL, PASSWORD, newEmail);
            fail();
        }catch(GRSException e){
            assertEquals("New email is same as old one.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateToInvalidEmail(){
        String newEmail = "customerEmail.com";

        Customer customer = null;
        try{
            customer = customerService.updateEmail(EMAIL, PASSWORD, newEmail);
            fail();
        }catch(GRSException e){
            assertEquals("Invalid new email.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateEmailCustomerNotFound(){
        String oldEmail = "oldEmail";
        String password = "testPassword";
        String newEmail = "customerEmail.com";

        Customer customer = null;
        try{
            customer = customerService.updateEmail(oldEmail, password, newEmail);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateEmailWrongPassword(){
        String password = "testPassword";
        String newEmail = "customerEmail.com";

        Customer customer = null;
        try{
            customer = customerService.updateEmail(EMAIL, password, newEmail);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdateEmailWrongOldEmail(){
        String oldEmail = "oldEmail";
        String newEmail = "customerEmail.com";

        Customer customer = null;
        try{
            customer = customerService.updateEmail(oldEmail, PASSWORD, newEmail);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdatePassword(){
        String newPassword = "newPassword";

        Customer customer = null;
        try{
            customer = customerService.updatePassword(EMAIL, PASSWORD, newPassword);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customer);
        assertEquals(newPassword, customer.getPassword());
    }

    @Test
    public void testUpdatePasswordToSame(){
        Customer customer = null;
        try{
            customer = customerService.updatePassword(EMAIL, PASSWORD, PASSWORD);
            fail();
        }catch(GRSException e){
            assertEquals("New password is same as old one.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdatePasswordToNull(){
        Customer customer = null;
        try{
            customer = customerService.updatePassword(EMAIL, PASSWORD, null);
            fail();
        }catch(GRSException e){
            assertEquals("Invalid new password.", e.getMessage());
        }
        assertNull(customer);
    }

    /*
     * Email and Password not in system
     */
    @Test
    public void testUpdatePasswordCustomerNotFound(){
        String email = "testEmail";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        Customer customer = null;
        try{
            customer = customerService.updatePassword(email, oldPassword, newPassword);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdatePasswordWrongEmail(){
        String email = "testEmail";
        String newPassword = "newPassword";

        Customer customer = null;
        try{
            customer = customerService.updatePassword(email, PASSWORD, newPassword);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testUpdatePasswordWrongOldPassword(){
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        Customer customer = null;
        try{
            customer = customerService.updatePassword(EMAIL, oldPassword, newPassword);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found, invalid email and password combination.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void getCustomer(){
        Customer customer = null;
        try{
            customer = customerService.getCustomerByEmail(EMAIL);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customer);
        assertEquals(EMAIL, customer.getEmail());
    }

    @Test
    public void getCustomerEmailNull(){
        Customer customer = null;
        try{
            customer = customerService.getCustomerByEmail(null);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void getCustomerEmailEmpty(){
        Customer customer = null;
        try{
            customer = customerService.getCustomerByEmail(" ");
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void getAllCustomersOne(){
        List<Customer> customers = new ArrayList<>();
        try{
            customers = customerService.getAllCustomers();
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(customers.stream().map(Customer::getEmail).collect(Collectors.toList()).contains(EMAIL));
    }

    @Test
    public void getAllCustomersMultiple(){
        List<Customer> addedCustomers = new ArrayList<>();
        for(int i = 0; i<3; i++){
            Customer customer = new Customer();
            customer.setEmail(i+"@email.com");
            customer.setPassword(Integer.toString(i));
            addedCustomers.add(customer);
        }
        when(customerRepository.findAll()).thenReturn(addedCustomers);

        List<Customer> customers = new ArrayList<>();
        try{
            customers = customerService.getAllCustomers();
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(customers.stream().map(Customer::getEmail).collect(Collectors.toList()).contains("0@email.com"));
        assertTrue(customers.stream().map(Customer::getEmail).collect(Collectors.toList()).contains("1@email.com"));
        assertTrue(customers.stream().map(Customer::getEmail).collect(Collectors.toList()).contains("2@email.com"));
    }

    @Test
    public void getAllCustomersNone(){
        List<Customer> addedCustomers = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(addedCustomers);

        List<Customer> customers = new ArrayList<>();
        try{
            customers = customerService.getAllCustomers();
            fail();
        }catch(GRSException e){
            assertEquals("No Customers found in the system.", e.getMessage());
        }
        assertEquals(0, customers.size());
    }

    @Test
    public void testDeleteCustomerByOwner() {
        Owner owner = new Owner();
        try {
            customerService.deleteCustomer(EMAIL, owner);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        verify(customerRepository, times(1)).deleteCustomerByEmail(EMAIL);
    }

    @Test
    public void testDeleteCustomerByInstructor() {
        Instructor instructor = new Instructor();
        try {
            customerService.deleteCustomer(EMAIL, instructor);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        verify(customerRepository, times(1)).deleteCustomerByEmail(EMAIL);
    }

    @Test
    public void testDeleteCustomerByHimself() {
        Customer customer = CUSTOMER;
        customer.setEmail(EMAIL);

        try {
            customerService.deleteCustomer(EMAIL, customer);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        verify(customerRepository, times(1)).deleteCustomerByEmail(EMAIL);
    }

    @Test
    public void testDeleteCustomerDoesNotExist() {
        String email = "testEmail";
        Owner owner = new Owner();
        try {
            customerService.deleteCustomer(email, owner);
            fail();
        } catch (GRSException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Customer not found.", e.getMessage());
            verify(customerRepository, times(0)).deleteCustomerByEmail(email);
        }
    }

    @Test
    public void testDeleteCustomerbyAnotherCustomer() {
        Customer customer = new Customer();
        String email = "testEmail";
        customer.setEmail(email);

        try {
            customerService.deleteCustomer(EMAIL, customer);
            fail();
        } catch (GRSException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("Customers can only be deleted by themselves or by instructors and owners.", e.getMessage());
            verify(customerRepository, times(0)).deleteCustomerByEmail(EMAIL);
        }
    }


    @Test
    public void testLoginCustomer(){
        Customer customer = null;
        try{
            customer = customerService.loginCustomer(EMAIL, PASSWORD);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customer);
        assertEquals(EMAIL, customer.getEmail());
        assertEquals(PASSWORD, customer.getPassword());
    }

    @Test
    public void testLoginCustomerNotInSystem(){
        String email = "testEmail";
        String password = "testPassword";

        Customer customer = null;
        try{
            customer = customerService.loginCustomer(email, password);
            fail();
        }catch(GRSException e){
            assertEquals("Invalid email or password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testLoginCustomerInvalidEmail(){
        String email = "testEmail";

        Customer customer = null;
        try{
            customer = customerService.loginCustomer(email, PASSWORD);
            fail();
        }catch(GRSException e){
            assertEquals("Invalid email or password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testLoginCustomerInvalidPassword(){
        String password = "testPassword";

        Customer customer = null;
        try{
            customer = customerService.loginCustomer(EMAIL, password);
            fail();
        }catch(GRSException e){
            assertEquals("Invalid email or password.", e.getMessage());
        }
        assertNull(customer);
    }

    @Test
    public void testChangeAccountType(){
        Owner owner = new Owner();

        Instructor instructor = null;
        try{
            instructor = customerService.changeAccountType(EMAIL, owner);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(instructor);
        assertEquals(EMAIL, instructor.getEmail());
        assertEquals(PASSWORD, instructor.getPassword());
        assertEquals(PERSON, instructor.getPerson());

        verify(customerRepository, times(1)).deleteCustomerByEmail(EMAIL);
    }

    @Test
    public void testChangeAccountTypeByInstructor(){
        Instructor instructor = null;
        try{
            instructor = customerService.changeAccountType(EMAIL, instructor);
            fail();
        }catch(GRSException e){
            assertEquals("Only owners can change account type.", e.getMessage());
        }
        assertNull(instructor);
    }

    @Test
    public void testChangeAccountTypeByCustomer(){
        Instructor instructor = null;
        try{
            instructor = customerService.changeAccountType(EMAIL, CUSTOMER);
            fail();
        }catch(GRSException e){
            assertEquals("Only owners can change account type.", e.getMessage());
        }
        assertNull(instructor);
    }

    @Test
    public void testChangeAccountTypeNoCustomerFound(){
        Customer customer = new Customer();
        String email = "testEmail";
        String password = "testPassword";
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setPerson(PERSON);

        Owner owner = new Owner();

        Instructor instructor = null;
        try{
            instructor = customerService.changeAccountType(email, owner);
            fail();
        }catch(GRSException e){
            assertEquals("Customer not found.", e.getMessage());
        }
        assertNull(instructor);
    }
}
