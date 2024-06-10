package ca.mcgill.ecse321.gymregistration.service;

import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.model.*;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestSessionService {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private InstructorRegistrationRepository instructorRegistrationRepository;
    @InjectMocks
    private SessionService sessionService;
    private static final int ID = 123;
    private static final Date DATE = new Date(2700000000L * 1000); //Random date
    private static final Time START_TIME = Time.valueOf("12:00:00");
    private static final Time END_TIME = Time.valueOf("15:00:00");
    private static final String DESCRIPTION = "Example session.";
    private static final String NAME = "Tom";
    private static final String LOCATION = "Montreal";
    private static final ClassType CLASS_TYPE = new ClassType("Pilates", true);
    private static final int CAPACITY = 50;
    private final Session SESSION = new Session();

    @BeforeEach
    public void setMockOutput() {
        lenient().when(sessionRepository.findAll()).thenAnswer((InvocationOnMock invocation) ->{
            SESSION.setId(ID);
            SESSION.setCapacity(CAPACITY);
            SESSION.setName(NAME);
            SESSION.setLocation(LOCATION);
            SESSION.setClassType(CLASS_TYPE);
            SESSION.setEndTime(END_TIME);
            SESSION.setStartTime(START_TIME);
            SESSION.setDescription(DESCRIPTION);
            SESSION.setDate(DATE);

            List<Session> sessionList = new ArrayList<>();
            sessionList.add(SESSION);
            return sessionList;
        });

        lenient().when(sessionRepository.findSessionById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0).equals(ID)){
                Session session = new Session();
                session.setId(ID);
                session.setCapacity(CAPACITY);
                session.setName(NAME);
                session.setLocation(LOCATION);
                session.setClassType(CLASS_TYPE);
                session.setEndTime(END_TIME);
                session.setStartTime(START_TIME);
                session.setDescription(DESCRIPTION);
                session.setDate(DATE);
                return session;
            } else{
                return null;
            }
        });
        lenient().when(instructorRegistrationRepository.findInstructorRegistrationsBySession_id(anyInt())).thenAnswer((InvocationOnMock invocation) ->{
            Instructor instructor = new Instructor("email", "1", new Person("Erik"));
            Session session = new Session();
            session.setId(ID);
            session.setCapacity(CAPACITY);
            session.setName(NAME);
            session.setLocation(LOCATION);
            session.setClassType(CLASS_TYPE);
            session.setEndTime(END_TIME);
            session.setStartTime(START_TIME);
            session.setDescription(DESCRIPTION);
            session.setDate(DATE);
            InstructorRegistration instructorRegistration = new InstructorRegistration(DATE, instructor, session);
            List<InstructorRegistration> instructorRegistrations = new ArrayList<>();
            instructorRegistrations.add(instructorRegistration);
            return instructorRegistrations;
        });
        lenient().when(sessionRepository.save(any(Session.class))).thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));
    }

    /**
     * Creating session success
     */
    @Test
    public void testCreateSession(){
        Date date = new Date(2800014000L * 1000);
        Time startTime = Time.valueOf("11:00:00");
        Time endTime = Time.valueOf("13:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
        } catch (GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(session);
        assertEquals(date, session.getDate());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertEquals(description, session.getDescription());
        assertEquals(name, session.getName());
        assertEquals(location, session.getLocation());
        assertEquals(classType, session.getClassType());
        assertEquals(capacity, session.getCapacity());
    }

    /**
     * creating session with taken time and date
     */
    @Test
    public void testCreateSessionTakenTime(){
        when(sessionRepository.findSessionByStartTimeAndDate(any(), any())).thenAnswer((InvocationOnMock invocation) -> {
                    SESSION.setId(ID);
                    SESSION.setCapacity(CAPACITY);
                    SESSION.setName(NAME);
                    SESSION.setLocation(LOCATION);
                    SESSION.setClassType(CLASS_TYPE);
                    SESSION.setEndTime(END_TIME);
                    SESSION.setStartTime(START_TIME);
                    SESSION.setDescription(DESCRIPTION);
                    SESSION.setDate(DATE);
                    return SESSION;
                });
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("12:00:00");
        Time endTime = Time.valueOf("13:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Time not available.");
        }
    }

    /**
     * creating session with null start time
     */
    @Test
    public void testCreateSessionNullStartTime(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = null;
        Time endTime = Time.valueOf("13:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }
    /**
     * creating session with null end time
     */
    @Test
    public void testCreateSessionNullEndTime(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = null;
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }

    /**
     * creating session with null class type
     */
    @Test
    public void testCreateSessionNullClassType(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("12:00:00");
        Time endTime = Time.valueOf("13:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = null;
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }
    /**
     * creating session with null date
     */
    @Test
    public void testCreateSessionNullDate(){
        Date date = null;
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }

    /**
     * creating session with null description
     */
    @Test
    public void testCreateSessionNullDescription(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = null;
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }
    /**
     * creating session with empty description
     */
    @Test
    public void testCreateSessionEmptyDescription(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }
    /**
     * creating session with null name
     */
    @Test
    public void testCreateSessionNullName(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = null;
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }
    /**
     * creating session with empty name
     */
    @Test
    public void testCreateSessionEmptyName(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = "";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }
    /**
     * creating session with null location
     */
    @Test
    public void testCreateSessionNullLocation(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = "Bob";
        String location = null;
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }

    /**
     * creating session with empty location
     */
    @Test
    public void testCreateSessionEmptyLocation(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = "Bob";
        String location = "";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Missing information.");
        }
    }

    /**
     * creating session with not approved class type
     */
    @Test
    public void testCreateSessionNotApproved(){
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", false);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Class must be approved.");
        }
    }

    /**
     * creating session with invalid date
     */
    @Test
    public void testCreateSessionInvalidDate(){
        Date date = new Date(1700000000L * 1000);
        Time startTime = Time.valueOf("13:00:00");
        Time endTime = Time.valueOf("14:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Owner owner = new Owner();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, owner);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Session must be at least 48 hours ahead of the current time.");
        }
    }

    /**
     * Creating session with instructor
     */
    @Test
    public void testCreateSessionInstructor(){
        Date date = new Date(2800014000L * 1000);
        Time startTime = Time.valueOf("11:00:00");
        Time endTime = Time.valueOf("13:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Instructor instructor = new Instructor();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, instructor);
        } catch (GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(session);
        assertEquals(date, session.getDate());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertEquals(description, session.getDescription());
        assertEquals(name, session.getName());
        assertEquals(location, session.getLocation());
        assertEquals(classType, session.getClassType());
        assertEquals(capacity, session.getCapacity());
    }

    /**
     * Creating session with customer
     */
    @Test
    public void testCreateSessionCustomer(){
        Date date = new Date(2800014000L * 1000);
        Time startTime = Time.valueOf("11:00:00");
        Time endTime = Time.valueOf("13:00:00");
        String description = "description";
        String name = "Bob";
        String location = "Montreal";
        ClassType classType = new ClassType("Aerobics", true);
        int capacity = 5;
        Customer customer = new Customer();
        Session session = null;
        try {
            session = sessionService.createSession(date, startTime, endTime, description, name, location, classType, capacity, customer);
            fail();
        } catch (GRSException e){
            assertNull(session);
            assertEquals(e.getMessage(), "Customers are not allowed to create sessions.");
        }
    }

    /**
     * test updating session
     */
    @Test
    public void testUpdateSession(){
        Session toUpdate = sessionRepository.findSessionById(123);
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(toUpdate.getId(), session, owner);
        } catch (Exception e){
            fail(e.getMessage());
        }
        assertNotNull(updated);
        assertEquals(date, session.getDate());
        assertEquals(startTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertEquals("new descr.", session.getDescription());
        assertEquals("Bob", session.getName());
        assertEquals("Ottawa", session.getLocation());
        assertEquals(classType, session.getClassType());
        assertEquals(3, session.getCapacity());
    }

    /**
     * test updating session when no existing session
     */
    @Test
    public void testUpdateSessionNoSession(){
        when(sessionRepository.findSessionById(anyInt())).thenAnswer((InvocationOnMock invocation) -> null);
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Session with id does not exist.");
            assertNull(updated);
        }
    }
    /**
     * test updating session when null session
     */
    @Test
    public void testUpdateSessionNullSession(){
        Session session = null;
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }

    /**
     * test updating session when null date
     */
    @Test
    public void testUpdateSessionNullDate(){
        Date date = null;
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }

    /**
     * test updating session when null startTime
     */
    @Test
    public void testUpdateSessionNullStartTime(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = null;
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }

    /**
     * test updating session when null end time
     */
    @Test
    public void testUpdateSessionNullEndTime(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = null;
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }

    /**
     * test updating session when null class type
     */
    @Test
    public void testUpdateSessionNullClassType(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = null;
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }
    /**
     * test updating session when null description
     */
    @Test
    public void testUpdateSessionNoDescription(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, null, "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }
    /**
     * test updating session when no name
     */
    @Test
    public void testUpdateSessionNoName(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", null, "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }
    /**
     * test updating session when no location
     */
    @Test
    public void testUpdateSessionNoLocation(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", null, classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Missing information.");
            assertNull(updated);
        }
    }
    /**
     * test updating session when class type not approved
     */
    @Test
    public void testUpdateSessionNotApproved(){
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", false);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Class must be approved.");
            assertNull(updated);
        }
    }
    /**
     * test updating session when invalid time
     */
    @Test
    public void testUpdateSessionInvalidTime(){
        Date date = new Date(1700000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Owner owner = new Owner();
        try{
            updated = sessionService.updateSession(ID, session, owner);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "Session must be at least 48 hours ahead of the current time.");
            assertNull(updated);
        }
    }

    /**
     * test updating session with instructor
     */
    @Test
    public void testUpdateSessionInstructor(){
        Session toUpdate = sessionRepository.findSessionById(123);
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Instructor instructor = new Instructor();
        try{
            updated = sessionService.updateSession(toUpdate.getId(), session, instructor);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "User does not have access to update session.");
            assertNull(updated);
        }
    }

    /**
     * test updating session with customer
     */
    @Test
    public void testUpdateSessionCustomer(){
        Session toUpdate = sessionRepository.findSessionById(123);
        Date date = new Date(2900000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 3);
        Session updated = null;
        Customer customer = new Customer();
        try{
            updated = sessionService.updateSession(toUpdate.getId(), session, customer);
            fail();
        } catch (Exception e){
            assertEquals(e.getMessage(), "User does not have access to update session.");
            assertNull(updated);
        }
    }

    /**
     * getting session
     */
    @Test
    public void testGetSession() {
        Session session = null;
        try {
            session = sessionService.getSessionById(ID);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertNotNull(session);
        assertEquals(NAME, session.getName());
        assertEquals(DATE, session.getDate());
        assertEquals(START_TIME, session.getStartTime());
        assertEquals(END_TIME, session.getEndTime());
        assertEquals(DESCRIPTION, session.getDescription());
        assertEquals(LOCATION, session.getLocation());
        assertEquals(CLASS_TYPE, session.getClassType());
        assertEquals(CAPACITY, session.getCapacity());
    }

    /**
     * getting session does not exist
     */
    @Test
    public void testGetSessionNone() {
        Session session = null;
        try {
            session = sessionService.getSessionById(0);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Session not found.");
        }
        assertNull(session);
    }

    /**
     * getting all sessions when one in database
     */
    @Test
    public void testGetAllSessionsOne(){
        List<Session> sessionList = new ArrayList<>();
        try {
            sessionList = sessionService.getAllSessions();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(sessionList.stream().map(Session::getId).collect(Collectors.toList()).contains(ID));
    }

    /**
     * getting all sessions when multiple in database
     */
    @Test
    public void testGetAllSessionsMultiple(){
        List<Session> addedSessions = new ArrayList<>();
        Date date = new Date(2700000000L * 1000);
        Time startTime = Time.valueOf("15:00:00");
        Time endTime = Time.valueOf("16:00:00");
        ClassType classType = new ClassType("Aerobics", true);
        Session session = new Session(date, startTime, endTime, "new descr.", "Bob", "Ottawa", classType, 5);
        addedSessions.add(session);
        Date date2 = new Date(2900000000L * 1000);
        Time startTime2 = Time.valueOf("15:00:00");
        Time endTime2 = Time.valueOf("16:00:00");
        ClassType classType2 = new ClassType("Aerobics", true);
        Session session2 = new Session(date2, startTime2, endTime2, "new descr.", "Bob", "Ottawa", classType2, 4);
        addedSessions.add(session2);
        when(sessionRepository.findAll()).thenReturn(addedSessions);

        List<Session> sessions = new ArrayList<>();
        try {
            sessions = sessionService.getAllSessions();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(sessions.stream().map(Session::getId).collect(Collectors.toList()).contains(session.getId()));
        assertTrue(sessions.stream().map(Session::getId).collect(Collectors.toList()).contains(session2.getId()));
    }

    /**
     * getting all sessions when none in database
     */
    @Test
    public void testGetAllSessionsNone(){
        List<Session> addedSessions = new ArrayList<>();
        when(sessionRepository.findAll()).thenReturn(addedSessions);

        List<Session> sessions = new ArrayList<>();
        try {
            sessions = sessionService.getAllSessions();
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "No Sessions found in the system.");
        }
        assertEquals(0, sessions.size());
    }

    /**
     * owner deleting session
     */
    @Test
    public void testDeleteSessionOwner() {
        Owner owner = new Owner("email", "1", new Person("Bob"));
        try {
            sessionService.deleteSession(ID, owner);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        verify(sessionRepository, times(1)).deleteSessionById(ID);
    }

    /**
     * customer deleting session
     */
    @Test
    public void testDeleteCustomer() {
        String creditCardNumber = "8765 4321 8765 4321";
        Customer customer = new Customer("email", "1", new Person("Bob"), creditCardNumber);
        
        try {
            sessionService.deleteSession(ID, customer);
            fail();
        } catch (Exception e) {
            verify(sessionRepository, times(0)).deleteSessionById(ID);
            assertEquals(e.getMessage(), "Customers can not delete sessions.");
        }
    }
    /**
     * no session to delete
     */
    @Test
    public void testDeleteNoSession() {
        Owner owner = new Owner("email", "1", new Person("Bob"));
        try {
            sessionService.deleteSession(0, owner);
            fail();
        } catch (Exception e) {
            verify(sessionRepository, times(0)).deleteSessionById(ID);
            assertEquals(e.getMessage(), "Session not found.");
        }
    }

    /**
     * instructor valid deleting session
     */
    @Test
    public void testDeleteSessionInstructorValid() {
        Instructor instructor = new Instructor("email", "1", new Person("Erik"));
        try {
            sessionService.deleteSession(ID, instructor);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        verify(sessionRepository, times(1)).deleteSessionById(ID);
    }
    /**
     * instructor invalid deleting session
     */
    @Test
    public void testDeleteSessionInstructorInvalid() {
        Instructor instructor = new Instructor("wrongEmail", "1", new Person("Bob"));
        try {
            sessionService.deleteSession(ID, instructor);
            fail();
        } catch (Exception e) {
            verify(sessionRepository, times(0)).deleteSessionById(ID);
            assertEquals(e.getMessage(), "Instructor does not have access to delete.");
        }
    }

}
