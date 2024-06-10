 /******************************************** 
    MANY OF THE METHODS IN THIS TEST VIOLATE THE 20 LINES CONSTRAINT
    DUE TO THE SHEER NUMBER OF FIELDS THAT MUST BE SET IT WAS UNAVOIDABLE
    THE FIRST 10 LINES OF MANY SESSIONS CAN BE IGNORED AS THEY ARE SIMPLY SETTING FIELDS
    ***************************************************/

package ca.mcgill.ecse321.gymregistration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.CustomerRegistration;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Session;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;


@ExtendWith(MockitoExtension.class)
public class TestCustomerRegistrationService {
    
    @Mock
    private CustomerRegistrationRepository customerRegistrationRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private static Calendar calendar;

    @InjectMocks
    private CustomerRegistrationService customerRegistrationService;

    private static final Customer CUSTOMER = new Customer(); 
    private static final String CUSTOMER_EMAIL = "customer@email.com";
    private static final String CREDIT = "1234 5678 1234 5678";

    private static final Customer CUSTOMER_OTHER = new Customer(); 
    private static final String CUSTOMER_EMAIL_OTHER = "otherCustomer@email.com";
    private static final int CREDIT_OTHER = 54321;


    private static final Session SESSION = new Session();
    private static final Session SESSION2 = new Session();

    private static final int SESSION_ID = 0;
    private static final int SESSION2_ID = 1;

    private static LocalDate sessionLocalDate = LocalDate.of(2025, 5, 1);
    private static final Date SESSION_DATE = Date.valueOf(sessionLocalDate);

    private static LocalTime sessionStartLocalTime = LocalTime.of(12, 0, 0);
    private static final Time START_TIME= Time.valueOf(sessionStartLocalTime);

    private static LocalTime sessionEndLocalTime = LocalTime.of(13, 0, 0);
    private static final Time END_TIME = Time.valueOf(sessionEndLocalTime);

    private static final int CAPACITY = 100;

    private static final CustomerRegistration CUSTOMER_REGISTRATION = new CustomerRegistration();
    private static final int CUSTOMER_REGISTRATION_ID = 0;
    private static Date CURRENT_DATE = new Date(System.currentTimeMillis());
    
    @BeforeEach
    public void setMockOutput(){

        lenient().when(customerRepository.findCustomerByEmail(anyString())).thenAnswer((InvocationOnMock invocation) ->{
            if( invocation.getArgument(0).equals(CUSTOMER_EMAIL)){
                Customer customer = new Customer();
                customer.setEmail(CUSTOMER.getEmail());
                customer.setCreditCardNumber(CUSTOMER.getCreditCardNumber());

                return customer;
            }else{
                return null;
            }
        });

        lenient().when(sessionRepository.findSessionById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);
            if(id == SESSION_ID){
                Session session = new Session();
                session.setId(SESSION.getId());
                session.setCapacity(SESSION.getCapacity());
                session.setDate(SESSION.getDate());
                session.setStartTime(SESSION.getStartTime());
                session.setEndTime(SESSION.getEndTime());

                return session;
            
            }else if(id == SESSION2_ID){
                Session session = new Session();
                session.setId(SESSION2.getId());
                session.setCapacity(SESSION2.getCapacity());
                session.setDate(SESSION2.getDate());
                session.setStartTime(SESSION2.getStartTime());
                session.setEndTime(SESSION2.getEndTime());

                return session;

            }else{
                return null;
            }
        });

        lenient().when(customerRegistrationRepository.findCustomerRegistrationsBySession_Id(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);
            if(id == SESSION_ID ){
                CustomerRegistration customerRegistration = new CustomerRegistration();
                customerRegistration.setId(CUSTOMER_REGISTRATION_ID);
                customerRegistration.setSession(SESSION);
                customerRegistration.setCustomer(CUSTOMER);
                customerRegistration.setDate(CURRENT_DATE);

                List<CustomerRegistration> customerRegistrationList= new ArrayList<>();
                customerRegistrationList.add(customerRegistration);
                return customerRegistrationList;

            }else if(id == SESSION2_ID){
                CustomerRegistration customerRegistration = new CustomerRegistration();
                customerRegistration.setId(CUSTOMER_REGISTRATION_ID);
                customerRegistration.setSession(SESSION2);
                customerRegistration.setCustomer(CUSTOMER);
                customerRegistration.setDate(CURRENT_DATE);

                List<CustomerRegistration> customerRegistrationList= new ArrayList<>();
                customerRegistrationList.add(customerRegistration);
                return customerRegistrationList;
            }
            else{
                return null;
            }
        });

        lenient().when(customerRegistrationRepository.findCustomerRegistrationById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);
            if(id == CUSTOMER_REGISTRATION_ID){
                CustomerRegistration customerRegistration = new CustomerRegistration();
                customerRegistration.setId(CUSTOMER_REGISTRATION_ID);
                customerRegistration.setSession(SESSION);
                customerRegistration.setCustomer(CUSTOMER);
                customerRegistration.setDate(CURRENT_DATE);

                return customerRegistration;
            }else{
                return null;
            }
        });

        lenient().when(customerRegistrationRepository.findCustomerRegistrationByCustomer_EmailAndSession_Id(anyString(), anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            String email = invocation.getArgument(0);
            int sessionId = invocation.getArgument(1);
            if(email.equals(CUSTOMER_EMAIL) && sessionId == SESSION_ID){
                    CustomerRegistration customerRegistration = new CustomerRegistration();
                    customerRegistration.setCustomer(CUSTOMER);
                    customerRegistration.setSession(SESSION);
                return customerRegistration;
            }else{
                return null;
            }
        });

        //might need to go
        lenient().when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenAnswer((InvocationOnMock invocation) -> {
            Session session = invocation.getArgument(1);
            Customer customer = invocation.getArgument(0);
            if(customer.getEmail().equals(CUSTOMER.getEmail()) && session.getId() == SESSION.getId()){
                CustomerRegistration customerRegistration = new CustomerRegistration();
                customerRegistration.setCustomer(CUSTOMER);
                customerRegistration.setSession(SESSION);
                return customerRegistration;
            }else{
                return null;
            }
        });

        
    }


    @Test
    public void testRegisterCustomerForSession(){
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);//make sure it's not the value we are expecting
        customer.setCreditCardNumber(CREDIT);

        CustomerRegistration customerRegistration = null;
        try{
            when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding for first time
            customerRegistration = customerRegistrationService.registerCustomerToSession(session.getId(), customer.getEmail());
        }catch (GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customerRegistration);
        assertEquals(SESSION_ID, customerRegistration.getSession().getId());
        assertEquals(CUSTOMER_EMAIL, customerRegistration.getCustomer().getEmail());
    }

    @Test
    public void testRegisterForNonexistentSession(){
        CustomerRegistration customerRegistration = null;
        try{
            customerRegistrationService.registerCustomerToSession(SESSION_ID+10, CUSTOMER_EMAIL);
            fail();
        }catch (GRSException e){
            assertEquals("Session not found.", e.getMessage());
        }
        assertNull(customerRegistration);
    }

    @Test
    public void testRegisterNonexistentCustomer(){
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        String email = "testEmail";

        CustomerRegistration customerRegistration = null;
        try{
            customerRegistrationService.registerCustomerToSession(SESSION_ID, "testEmail");
            fail();
        }catch (GRSException e){
            assertEquals("Customer not found.", e.getMessage());
        }
        assertNull(customerRegistration);
    }

    @Test
    public void testRegisterForPastSession(){
        LocalDate sessionLocalDate = LocalDate.of(2023, 5, 1);
        Date sessionDate = Date.valueOf(sessionLocalDate);

        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(sessionDate);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        CustomerRegistration customerRegistration = null;
        try{
            when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding for first time
            customerRegistrationService.registerCustomerToSession(SESSION_ID, CUSTOMER_EMAIL);
            fail();
        }catch (GRSException e){
            assertEquals("Cannot register for completed session.", e.getMessage());
        }
        assertNull(customerRegistration);
    }

    @Test
    public void testRegisterForInProgress(){
        // Setting start time to now
        LocalTime currentTime = LocalTime.now();
        Time startTime = Time.valueOf(currentTime);

        // Setting end time in an hour
        LocalTime inAnHour = currentTime.plusHours(1);
        Time endTime = Time.valueOf(inAnHour);

        // Setting date to current date
        LocalDate currentDateTime = LocalDate.now();
        Date sessionDate = Date.valueOf(currentDateTime);

        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(sessionDate);
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        CustomerRegistration customerRegistration = null;
        try{
            when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding for first time
            customerRegistrationService.registerCustomerToSession(session.getId(), CUSTOMER_EMAIL);
            fail();
        }catch (GRSException e){
            assertEquals("Cannot register for in-progress session.", e.getMessage());
        }
        assertNull(customerRegistration);
    }

    @Test
    public void testRegisterForSessionNullCreditInformation(){
        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(null);

        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        CustomerRegistration customerRegistration = null;
        try{
            customerRegistrationService.registerCustomerToSession(session.getId(), customer.getEmail());
            fail();
        }catch (GRSException e){
            assertEquals("Credit card must be entered to register for a class.", e.getMessage());
        }
        assertNull(customerRegistration);
    }

    @Test
    public void testRegisterForSessionEmptyCreditInformation(){
        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber("");

        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        CustomerRegistration customerRegistration = null;
        try{
            customerRegistrationService.registerCustomerToSession(session.getId(), customer.getEmail());
            fail();
        }catch (GRSException e){
            assertEquals("Credit card must be entered to register for a class.", e.getMessage());
        }
        assertNull(customerRegistration);
    }
    
    @Test
    public void testRegisterForSessionAtCapacity(){
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);
    
        List<CustomerRegistration> registrations = new ArrayList<>();
        for (int i=0; i<session.getCapacity(); i++){
            Customer addedCustomer = new Customer();
            addedCustomer.setEmail(i+"@email.com");
            
            // assign credit card number for each customer
            switch (String.valueOf(i).length()) {
                case 1:
                    addedCustomer.setCreditCardNumber(String.valueOf(i) + "000 5678 1234 5678");
                    break;
            
                case 2:
                    addedCustomer.setCreditCardNumber(String.valueOf(i) + "00 5678 1234 5678");
                    break;

                case 3:
                    addedCustomer.setCreditCardNumber(String.valueOf(i) + "0 5678 1234 5678");
                    break;

                default:
                    break;
            }

            when(customerRepository.findCustomerByEmail(anyString())).thenReturn(addedCustomer);

            CustomerRegistration addedRegistration = new CustomerRegistration();
            addedRegistration = customerRegistrationService.registerCustomerToSession(session.getId(), addedCustomer.getEmail());
            registrations.add(addedRegistration);
        }
        
        when(customerRegistrationRepository.findCustomerRegistrationsBySession_Id(session.getId())).thenReturn(registrations);

        Customer customer = new Customer();
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        CustomerRegistration customerRegistration = null;
        try{
            customerRegistrationService.registerCustomerToSession(session.getId(), customer.getEmail());
            fail();
        }catch (GRSException e){
            assertEquals("Session is already at capacity.", e.getMessage());
        }
        assertNull(customerRegistration);
    }

    @Test
    public void testRegisterForSessionTwice(){
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding for first time
        CustomerRegistration firstCustomerRegistration = customerRegistrationService.registerCustomerToSession(session.getId(), customer.getEmail());
        
        when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(firstCustomerRegistration);
        CustomerRegistration newCustomerRegistration = null;
        try{
            newCustomerRegistration = customerRegistrationService.registerCustomerToSession(session.getId(), customer.getEmail());
            fail();
        }catch(GRSException e){
            assertEquals("Cannot register for same session twice.", e.getMessage());
        }
        assertNull(newCustomerRegistration);
    }

    @Test
    public void testGetRegistration(){
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);
        
        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        CustomerRegistration customerRegistration = null;
        try{
            customerRegistration = customerRegistrationService.getCustomerRegistrationByCustomerAndSession(SESSION_ID, CUSTOMER_EMAIL);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customerRegistration);
        assertEquals(SESSION_ID, customerRegistration.getSession().getId());
        assertEquals(CUSTOMER_EMAIL, customerRegistration.getCustomer().getEmail());
    }

    @Test
    public void testUpdateRegistration(){
        Session oldSession = SESSION;
        oldSession.setId(SESSION_ID);
        oldSession.setCapacity(CAPACITY);
        oldSession.setDate(SESSION_DATE);
        oldSession.setStartTime(START_TIME);
        oldSession.setEndTime(END_TIME);

        Session newSession = SESSION2;
        newSession.setId(1);
        newSession.setCapacity(CAPACITY);
        newSession.setDate(SESSION_DATE);
        newSession.setStartTime(START_TIME);
        newSession.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);
        when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding for first time
        CustomerRegistration customerRegistration = customerRegistrationService.registerCustomerToSession(oldSession.getId(), customer.getEmail());
        lenient().when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(customerRegistration);
        try{
            customerRegistration = customerRegistrationService.updateCustomerRegistration(oldSession.getId(), newSession.getId(), customer.getEmail());
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(customerRegistration);
        assertEquals(SESSION_ID, customerRegistration.getSession().getId());
    }
    /******************************************** 
    //WE UNDERSTAND THIS VIOLATES THE 20 LINES CONSTRAINT
    //DUE TO THE SHEER NUMBER OF FIELDS THAT MUST BE SET IT WAS UNAVOIDABLE
    ***************************************************/
    @Test
    public void testGetRegistrationsByCustomer(){
        Session firstSession = SESSION;
        firstSession.setId(SESSION_ID);
        firstSession.setCapacity(CAPACITY);
        firstSession.setDate(SESSION_DATE);
        firstSession.setStartTime(START_TIME);
        firstSession.setEndTime(END_TIME);

        Session secondSession = SESSION2;
        secondSession.setId(1);
        secondSession.setCapacity(CAPACITY);
        secondSession.setDate(SESSION_DATE);
        secondSession.setStartTime(START_TIME);
        secondSession.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        List<CustomerRegistration> addedRegistrations = new ArrayList<>();
        when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding for first time
        CustomerRegistration registrationToFirstSession = customerRegistrationService.registerCustomerToSession(firstSession.getId(), customer.getEmail());
        when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class),  any(Session.class))).thenReturn(null);//mock adding new
        CustomerRegistration registrationToSecondSession = customerRegistrationService.registerCustomerToSession(secondSession.getId(), customer.getEmail());
        registrationToSecondSession.setId(1);//set proper id value
        addedRegistrations.add(registrationToFirstSession);
        addedRegistrations.add(registrationToSecondSession);

        lenient().when(customerRegistrationRepository.findCustomerRegistrationsByCustomer_Email(anyString())).thenReturn(addedRegistrations);

        List<CustomerRegistration> customerRegistrations = new ArrayList<>();
        try{
            customerRegistrations = customerRegistrationService.getCustomerRegistrationsByCustomer(customer.getEmail());
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertEquals(0, customerRegistrations.get(0).getId());
        assertEquals(1, customerRegistrations.get(1).getId());
        
        assertEquals(firstSession.getId(), customerRegistrations.get(0).getSession().getId());
        assertEquals(secondSession.getId(), customerRegistrations.get(1).getSession().getId());
    }

    @Test
    public void testGetRegistrationsBySession(){
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer1 = CUSTOMER;
        customer1.setEmail(CUSTOMER_EMAIL);
        customer1.setCreditCardNumber(CREDIT);

        Customer customer2 = CUSTOMER;
        customer2.setEmail(CUSTOMER_EMAIL); //Same customers here have to mock solution
        customer2.setCreditCardNumber(CREDIT);

        List<CustomerRegistration> addedRegistrations = new ArrayList<>();
        lenient().when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class), any(Session.class))).thenReturn(null);//mock first addition
        CustomerRegistration registrationToFirstSession = customerRegistrationService.registerCustomerToSession(session.getId(), customer1.getEmail());
        lenient().when(customerRegistrationRepository.findCustomerRegistrationByCustomerAndSession(any(Customer.class), any(Session.class))).thenReturn(null);//mock first addition
        CustomerRegistration registrationToSecondSession = customerRegistrationService.registerCustomerToSession(session.getId(), customer2.getEmail());
        registrationToSecondSession.setId(1);//mock sets to zero incorrectly 
        registrationToSecondSession.setCustomer(customer2);
        registrationToSecondSession.setCustomer(customer1);
        addedRegistrations.add(registrationToFirstSession);
        addedRegistrations.add(registrationToSecondSession);

        lenient().when(customerRegistrationRepository.findCustomerRegistrationsBySession_Id(anyInt())).thenReturn(addedRegistrations);

        List<CustomerRegistration> customerRegistrations = new ArrayList<>();
        try{
            customerRegistrations = customerRegistrationService.getCustomerRegistrationsBySession(session.getId());
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(customerRegistrations.stream().map(CustomerRegistration::getId).collect(Collectors.toList()).contains(0));
        assertTrue(customerRegistrations.stream().map(CustomerRegistration::getId).collect(Collectors.toList()).contains(1));

        assertTrue(customerRegistrations.stream().map(CustomerRegistration::getCustomer).collect(Collectors.toList()).contains(customer1));
        assertTrue(customerRegistrations.stream().map(CustomerRegistration::getCustomer).collect(Collectors.toList()).contains(customer2));
    }

    @Test
    public void testRemoveCustomerFromSessionInvalidSessionId()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

       
        try{
        customerRegistrationService.removeCustomerFromSession(-1, customer.getEmail(), customer);}
        catch(GRSException e){
            assertEquals("No session or customer entered.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerFromSessionNullEmail()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        
        try{
            customerRegistrationService.removeCustomerFromSession(session.getId(), null,customer);}
        catch(GRSException e){
            assertEquals("No session or customer entered.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerFromSessionInvalidPermissions()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);
        try{
            customerRegistrationService.removeCustomerFromSession(session.getId(), customer.getEmail() + " ", customer);}
        catch(GRSException e){
            assertEquals("Customers can only be removed from sessions by the owner, instructor or themselves.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerFromSessionNonExistantCustomer()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);
        
        customerRegistrationRepository.save(new CustomerRegistration(SESSION_DATE, SESSION, CUSTOMER));
        try{
            customerRegistrationService.removeCustomerFromSession(session.getId(), customer.getEmail()+"wrong", new Owner("email", "password", null));}
        catch(GRSException e){
            assertEquals("Customer not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerFromSessionNonExistantSession()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);
        
        customerRepository.save(customer);
        customerRegistrationRepository.save(new CustomerRegistration(SESSION_DATE, SESSION, CUSTOMER));
        
        try{
            customerRegistrationService.removeCustomerFromSession(session.getId()+1000, customer.getEmail(), customer);}
        catch(GRSException e){
            assertEquals("Session not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerFromSession()
    {
        lenient().doAnswer(invocation -> {
            return null; // Simulate successful deletion
        }).when(customerRegistrationRepository).delete(any(CustomerRegistration.class));
        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);
        customerRegistrationService.removeCustomerFromSession(session.getId(), customer.getEmail(), customer);
        assertEquals(1,1); // if no error then pass;
    }
    @Test
    public void testRemoveCustomerFromAllSessionsInvalidCustomer()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL+" ");
        customer.setCreditCardNumber(CREDIT);

        try{
            customerRegistrationService.removeCustomerFromAllSessions(customer.getEmail(), customer);}
        catch(GRSException e){
            assertEquals("Customer not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerFromAllSessionsInvalidPermissions()
    {
        Session session = SESSION;
        session.setId(SESSION_ID);
        session.setCapacity(CAPACITY);
        session.setDate(SESSION_DATE);
        session.setStartTime(START_TIME);
        session.setEndTime(END_TIME);

        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);

        try{
            customerRegistrationService.removeCustomerFromAllSessions(customer.getEmail()+"wrong", customer);}
        catch(GRSException e){
            assertEquals("Customers can only remove themselves from sessions.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveCustomerAllFromSessions()
    {
        lenient().doAnswer(invocation -> {
            return null; // Simulate successful deletion
        }).when(customerRegistrationRepository).delete(any(CustomerRegistration.class));
        Customer customer = CUSTOMER;
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setCreditCardNumber(CREDIT);
        customerRegistrationService.removeCustomerFromAllSessions(customer.getEmail(), customer);
        assertEquals(1,1); // if no error then pass;
    }
}
