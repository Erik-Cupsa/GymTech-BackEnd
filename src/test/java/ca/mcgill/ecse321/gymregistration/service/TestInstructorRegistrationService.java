package ca.mcgill.ecse321.gymregistration.service;

import ca.mcgill.ecse321.gymregistration.dao.InstructorRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.SessionRepository;
import ca.mcgill.ecse321.gymregistration.dto.InstructorRegistrationDto;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.InstructorRegistration;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Session;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

//
@ExtendWith(MockitoExtension.class)
public class TestInstructorRegistrationService {
    @Mock
    private InstructorRegistrationRepository instructorRegistrationRepository;
    @Mock
    private InstructorRepository instructorRepository;
    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private static InstructorRegistrationService instructorRegistrationService = new InstructorRegistrationService();

    private static final InstructorRegistration INSTRUCTORREGISTRATION = new InstructorRegistration();

    private static final int ID = INSTRUCTORREGISTRATION.getId();

    private static final Instructor INSTRUCTOR = new Instructor();

    private static final int INSTRUCTOR_ID = INSTRUCTOR.getId();

    private static final Session SESSION = new Session();

    private static final int SESSION_ID = SESSION.getId();

    private static final String EMAIL = "email@example.com";

    @BeforeEach
    public void setMockOutput() {
        // Stubbing findInstructorById method
        lenient().when(instructorRepository.findInstructorById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);
            if (id == INSTRUCTOR_ID) { // Replace INSTRUCTOR_ID with your expected instructor ID
                Instructor instructor = new Instructor();
                instructor.setId(id);
                instructor.setEmail(null);
                return instructor;
            } else {
                return null;
            }
        });

        lenient().when(instructorRepository.findInstructorByEmail(anyString()))
                .thenAnswer((InvocationOnMock invocation) -> {
                    String email = invocation.getArgument(0);
                    if (email == EMAIL) { // Replace INSTRUCTOR_ID with your expected instructor ID
                        Instructor instructor = new Instructor();
                        instructor.setEmail(email);
                        return instructor;
                    } else {
                        return null;
                    }
                });

        // Stubbing findSessionById method
        lenient().when(sessionRepository.findSessionById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);
            if (id == SESSION_ID) { // Replace SESSION_ID with your expected session ID
                Session session = new Session();
                session.setId(id);
                return session;
            } else {
                return null;
            }
        });

        // Stubbing findInstructorRegistrationById method
        lenient().when(instructorRegistrationRepository.findInstructorRegistrationById(anyInt()))
                .thenAnswer((InvocationOnMock invocation) -> {
                    int id = invocation.getArgument(0);
                    if (id == ID) {
                        InstructorRegistration instructorRegistration = new InstructorRegistration();
                        instructorRegistration.setId(ID);
                        return instructorRegistration;
                    } else {
                        return null;
                    }
                });

        lenient().when(instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(anyInt(),
                anyInt())).thenAnswer((InvocationOnMock invocation) -> {
                    int instructorId = invocation.getArgument(0);
                    int sessionId = invocation.getArgument(1);

                    if (instructorId == INSTRUCTOR_ID && sessionId == SESSION_ID) {
                        InstructorRegistration instructorRegistration = new InstructorRegistration();
                        instructorRegistration.setSession(SESSION);

                        instructorRegistration.setInstructor(INSTRUCTOR);
                        instructorRegistration.getInstructor().setEmail(EMAIL);
                        return instructorRegistration;
                    } else {
                        return null;
                    }
                });

        // Stubbing save methods to return the saved object
        lenient().when(instructorRegistrationRepository.save(any(InstructorRegistration.class)))
                .thenAnswer((InvocationOnMock invocation) -> {
                    return invocation.getArgument(0); // Return the saved object
                });
        lenient().when(instructorRepository.save(any(Instructor.class))).thenAnswer((InvocationOnMock invocation) -> {
            return invocation.getArgument(0); // Return the saved object
        });
        lenient().when(sessionRepository.save(any(Session.class))).thenAnswer((InvocationOnMock invocation) -> {
            return invocation.getArgument(0); // Return the saved object
        });

        // Whenever anything is saved, just return the parameter object

    }

    @Test
    public void testRegisterInstructorForClass() {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        lenient()
                .when(instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(anyInt(),
                        anyInt()))
                .thenReturn(null);// for the first run
        InstructorRegistration instructorRegistration = instructorRegistrationService
                .registerInstructorForClass(session.getId(), instructor.getEmail(), new Owner());
        assertNotNull(instructorRegistration);
        assertEquals(session.getId(), instructorRegistration.getSession().getId());
        assertEquals(instructor.getId(), instructorRegistration.getInstructor().getId());
    }

    @Test
    public void testRemoveInstructorFromClass() {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);

        instructorRepository.save(instructor);
        sessionRepository.save(session);
        lenient()
                .when(instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(anyInt(),
                        anyInt()))
                .thenReturn(null);

        InstructorRegistration instructorRegistration = instructorRegistrationService
                .registerInstructorForClass(session.getId(), instructor.getEmail(), instructor);
        List<InstructorRegistration> instructorRegistrationstest = new ArrayList<>();
        instructorRegistrationstest.add(instructorRegistration);
        when(instructorRegistrationRepository.findInstructorRegistrationsBySession_id(anyInt()))
                .thenReturn(instructorRegistrationstest);
        try {
            instructorRegistrationService.removeInstructorFromClass(instructorRegistration.getSession().getId(),
                    instructorRegistration.getInstructor().getEmail(), instructor);
        } catch (GRSException e) {
            assertEquals("Not enough instructors registered.", e.getMessage());
        }
    }

    @Test
    public void testRegisterInstructorForClassInvalidInstructor() {
        Session session = new Session();
        Instructor instructor = new Instructor();

        try {
            instructorRegistrationService.registerInstructorForClass(1000, null, new Owner());
        } catch (GRSException e) {
            assertEquals("Instructor not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRegisterInstructorForClassInvalidSession() {
        Instructor instructor = new Instructor();
        instructor.setEmail(EMAIL);
        instructor = instructorRepository.save(instructor);
        try {
            instructorRegistrationService.registerInstructorForClass(1000, EMAIL, instructor);
        } catch (GRSException e) {
            assertEquals("Session not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRegisterInstructorForClassAlreadyRegistered() {
        Session session = new Session();
        Instructor instructor = new Instructor();
        instructor.setEmail(EMAIL);
        instructorRepository.save(instructor);
        sessionRepository.save(session);

        try {
            instructorRegistrationService.registerInstructorForClass(session.getId(), instructor.getEmail(),
                    instructor);
        } catch (GRSException e) {
            assertEquals("Already registered.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRegisterInstructorForClassInvalidPermissions() {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        lenient()
                .when(instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(anyInt(),
                        anyInt()))
                .thenReturn(null);// for the first run
        try {
            InstructorRegistration instructorRegistration = instructorRegistrationService.registerInstructorForClass(
                    session.getId(), instructor.getEmail(), new Instructor("WrongEmail@email.com", null, null));
        } catch (GRSException e) {
            assertEquals("Instructors can only be assigned by themselves or by the owner.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testRemoveInstructorFromClassInvalidInstructor() {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructorRepository.save(instructor);
        sessionRepository.save(session);
        InstructorRegistration instructorRegistration = new InstructorRegistration();
        instructorRegistration.setSession(session);
        instructorRegistration.setInstructor(instructor);
        Session session1 = new Session();
        Instructor instructor1 = new Instructor(EMAIL + "1", null, null);
        instructorRepository.save(instructor1);
        sessionRepository.save(session1);
        InstructorRegistration instructorRegistration1 = new InstructorRegistration();
        instructorRegistration.setSession(session1);
        instructorRegistration.setInstructor(instructor1);
        List<InstructorRegistration> instructorRegistrationstest = new ArrayList<>();
        instructorRegistrationstest.add(instructorRegistration);
        instructorRegistrationstest.add(instructorRegistration1);
        when(instructorRegistrationRepository.findInstructorRegistrationsBySession_id(anyInt()))
                .thenReturn(instructorRegistrationstest);
        try {
            instructorRegistrationService.removeInstructorFromClass(instructorRegistration.getSession().getId(),
                    "fake email", new Owner());
        } catch (GRSException e) {
            assertEquals("Cannot remove Instructor.", e.getMessage());
        }
    }

    public void testRemoveInstructorFromClassInvalidPermissions() {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructorRepository.save(instructor);
        sessionRepository.save(session);
        InstructorRegistration instructorRegistration = new InstructorRegistration();
        instructorRegistration.setSession(session);
        instructorRegistration.setInstructor(instructor);
        Session session1 = new Session();
        Instructor instructor1 = new Instructor(EMAIL + "1", null, null);
        instructorRepository.save(instructor1);
        sessionRepository.save(session1);
        InstructorRegistration instructorRegistration1 = new InstructorRegistration();
        instructorRegistration.setSession(session1);
        instructorRegistration.setInstructor(instructor1);
        List<InstructorRegistration> instructorRegistrationstest = new ArrayList<>();
        instructorRegistrationstest.add(instructorRegistration);
        instructorRegistrationstest.add(instructorRegistration1);
        when(instructorRegistrationRepository.findInstructorRegistrationsBySession_id(anyInt()))
                .thenReturn(instructorRegistrationstest);
        try {
            instructorRegistrationService.removeInstructorFromClass(instructorRegistration.getSession().getId(),
                    "fake email", null);
        } catch (GRSException e) {
            assertEquals("Cannot remove Instructor.", e.getMessage());
        }
    }

    @Test
    public void testRemoveInstructorFromClassInvalidSession() {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructorRepository.save(instructor);
        sessionRepository.save(session);
        InstructorRegistration instructorRegistration = new InstructorRegistration();
        instructorRegistration.setSession(session);
        instructorRegistration.setInstructor(instructor);
        Session session1 = new Session();
        Instructor instructor1 = new Instructor(EMAIL + "1", null, null);
        instructorRepository.save(instructor1);
        sessionRepository.save(session1);
        InstructorRegistration instructorRegistration1 = new InstructorRegistration();
        instructorRegistration.setSession(session1);
        instructorRegistration.setInstructor(instructor1);
        List<InstructorRegistration> instructorRegistrationstest = new ArrayList<>();

        when(instructorRegistrationRepository.findInstructorRegistrationsBySession_id(anyInt()))
                .thenReturn(instructorRegistrationstest);
        try {
            instructorRegistrationService.removeInstructorFromClass(1000, EMAIL, new Owner());
        } catch (GRSException e) {
            assertEquals("Sessions not found.", e.getMessage());
        }
    }

    @Test
    public void testGetInstructorRegistration() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);

        InstructorRegistration instructorRegistration = instructorRegistrationService
                .getInstructorRegistrationByInstructorAndSession(SESSION_ID, EMAIL);
        assertNotNull(instructorRegistration);
        assertEquals(EMAIL, instructorRegistration.getInstructor().getEmail());
        assertEquals(SESSION_ID, instructorRegistration.getSession().getId());
    }

    @Test
    public void testGetInstructorRegistrationByInstructor() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        Session session2 =new Session();
        session2.setId(1);
        InstructorRegistration instructorRegistration = new InstructorRegistration(null,instructor,session);
        InstructorRegistration instructorRegistration2 = new InstructorRegistration(null,instructor,session2);
        List<InstructorRegistration> instructorRegistrations = new ArrayList<>();
        instructorRegistrations.add(instructorRegistration);
        instructorRegistrations.add(instructorRegistration2);
        lenient().when(instructorRegistrationRepository.findInstructorRegistrationsByInstructor_Email(anyString())).thenReturn(instructorRegistrations);

        List<InstructorRegistration> instructorRegistrationsReturn = instructorRegistrationService.getInstructorRegistrationsByInstructor(EMAIL);

        assertNotNull(instructorRegistrationsReturn);
        assertTrue(instructorRegistrations.contains(instructorRegistration));
        assertTrue(instructorRegistrations.contains(instructorRegistration2));
    }
    @Test
    public void testGetInstructorRegistrationByInstructorNull() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        Session session2 =new Session();
        session2.setId(1);
        InstructorRegistration instructorRegistration = new InstructorRegistration(null,instructor,session);
        InstructorRegistration instructorRegistration2 = new InstructorRegistration(null,instructor,session2);
        List<InstructorRegistration> instructorRegistrations = new ArrayList<>();
        instructorRegistrations.add(instructorRegistration);
        instructorRegistrations.add(instructorRegistration2);
        lenient().when(instructorRegistrationRepository.findInstructorRegistrationsByInstructor_Email(anyString())).thenReturn(instructorRegistrations);
        try{
        List<InstructorRegistration> instructorRegistrationsReturn = instructorRegistrationService.getInstructorRegistrationsByInstructor(null);
        } catch(GRSException e)
        {
            assertEquals("No instructor enterred.", e.getMessage());
            return;
        }
        fail();
    }
    @Test
    public void testGetInstructorRegistrationByInstructorInvalidEmail() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        Session session2 =new Session();
        session2.setId(1);
        InstructorRegistration instructorRegistration = new InstructorRegistration(null,instructor,session);
        InstructorRegistration instructorRegistration2 = new InstructorRegistration(null,instructor,session2);
        List<InstructorRegistration> instructorRegistrations = new ArrayList<>();
        instructorRegistrations.add(instructorRegistration);
        instructorRegistrations.add(instructorRegistration2);
        lenient().when(instructorRegistrationRepository.findInstructorRegistrationsByInstructor_Email(anyString())).thenReturn(instructorRegistrations);
        try{
        List<InstructorRegistration> instructorRegistrationsReturn = instructorRegistrationService.getInstructorRegistrationsByInstructor(EMAIL+"  ");
        } catch(GRSException e)
        {
            assertEquals("Instructor not found.", e.getMessage());
            return;
        }
        fail();
    }
    @Test
    public void testGetInstructorRegistrationByNoRegistrations() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        Session session2 =new Session();
        session2.setId(1);
        InstructorRegistration instructorRegistration = new InstructorRegistration(null,instructor,session);
        InstructorRegistration instructorRegistration2 = new InstructorRegistration(null,instructor,session2);
        List<InstructorRegistration> instructorRegistrations = new ArrayList<>();
        lenient().when(instructorRegistrationRepository.findInstructorRegistrationsByInstructor_Email(anyString())).thenReturn(instructorRegistrations);
        try{
        List<InstructorRegistration> instructorRegistrationsReturn = instructorRegistrationService.getInstructorRegistrationsByInstructor(EMAIL);
        } catch(GRSException e)
        {
            assertEquals("No registrations found in the system.", e.getMessage());
            return;
        }
        fail();
    }

    

    @Test
    public void testGetInstructorRegistrationInvalidEmail() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        try {
            InstructorRegistration instructorRegistration = instructorRegistrationService
                    .getInstructorRegistrationByInstructorAndSession(SESSION_ID, null);
        } catch (GRSException e) {
            assertEquals("No instructor or session entered.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testGetNonExistantInstructorRegistration() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        try {
            InstructorRegistration instructorRegistration = instructorRegistrationService
                    .getInstructorRegistrationByInstructorAndSession(SESSION_ID, EMAIL + " ");
        } catch (GRSException e) {
            assertEquals("Instructor not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testGetInstructorRegistrationInvalidSessionId() {
        Session session = new Session();
        session.setId(SESSION_ID);
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        try {
            InstructorRegistration instructorRegistration = instructorRegistrationService
                    .getInstructorRegistrationByInstructorAndSession(1000, EMAIL);
        } catch (GRSException e) {
            assertEquals("Session not found.", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testUpdateInstructorRegistration()
    {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        lenient()
                .when(instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(anyInt(),
                        anyInt()))
                .thenReturn(null);// for the first run
        InstructorRegistration instructorRegistration = instructorRegistrationService
                .registerInstructorForClass(session.getId(), instructor.getEmail(), new Owner());
        
        InstructorRegistration toUpdate = new InstructorRegistration(null, new Instructor("newemail", "password", null), session);
        InstructorRegistrationDto instructorRegistrationDto = new InstructorRegistrationDto();

        instructorRegistration = instructorRegistrationService.updateInstructorRegistration(ID, instructorRegistrationDto);
        assertNotNull(toUpdate);
        assertEquals("newemail",toUpdate.getInstructor().getEmail());
    }

    public void testUpdateInstructorRegistrationInvalidEmail()
    {
        Session session = new Session();
        Instructor instructor = new Instructor(EMAIL, null, null);
        instructor = instructorRepository.save(instructor);
        sessionRepository.save(session);
        lenient()
                .when(instructorRegistrationRepository.findInstructorRegistrationByInstructor_idAndSession_id(anyInt(),
                        anyInt()))
                .thenReturn(null);// for the first run
        InstructorRegistration instructorRegistration = instructorRegistrationService
                .registerInstructorForClass(session.getId(), instructor.getEmail(), new Owner());
        
        InstructorRegistration toUpdate = new InstructorRegistration(null, new Instructor(null, "password", null), session);
        InstructorRegistrationDto instructorRegistrationDto = new InstructorRegistrationDto();
        try{
        instructorRegistration = instructorRegistrationService.updateInstructorRegistration(ID, instructorRegistrationDto);
        }
        catch(GRSException e)
        {
            assertEquals("Instructor Registration not found.", e.getMessage());
            return;
        }
        fail();
    }
}
