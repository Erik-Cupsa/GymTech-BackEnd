package ca.mcgill.ecse321.gymregistration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Person;

import java.util.List;

@SpringBootTest
public class InstructorTests {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private InstructorRepository instructorRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        instructorRepository.deleteAll();
        personRepository.deleteAll();
    }
    @Test
    public void testCreateAndReadInstructor() {
        // Create and persist person.
        Person joe = createAndPersistPerson("Joe Smith");

        // Create instructor.
        Instructor instructor = createAndPersistInstructor("instructoremail@emailprovider.ca", "instructorPassword", joe);

        // Read instructor from database.
        Instructor instructorFromDB = instructorRepository.findInstructorById(instructor.getId());

        // Assertions
        assertInstructorAttributes(instructor, instructorFromDB);
        assertPersonAttributes(joe, instructorFromDB.getPerson());
    }

    @Test
    public void testFindInstructorsByPersonName() {
        // Create and persist person.
        Person john = createAndPersistPerson("John Doe");

        // Create instructors with the same person.
        createAndPersistInstructor("instructor1@emailprovider.ca", "instructor1Password", john);
        createAndPersistInstructor("instructor2@emailprovider.ca", "instructor2Password", john);

        // Find instructors by person name.
        List<Instructor> instructors =  instructorRepository.findInstructorsByPerson_Name(john.getName());

        // Assertions
        assertInstructorsListAttributes(instructors);
    }

    private Person createAndPersistPerson(String name) {
        Person person = new Person(name);
        return personRepository.save(person);
    }

    private Instructor createAndPersistInstructor(String email, String password, Person person) {
        Instructor instructor = new Instructor(email, password, person);
        return instructorRepository.save(instructor);
    }

    private void assertInstructorAttributes(Instructor expected, Instructor actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPassword(), actual.getPassword());
    }

    private void assertPersonAttributes(Person expected, Person actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    private void assertInstructorsListAttributes(List<Instructor> instructors) {
        assertNotNull(instructors);
        assertEquals(2, instructors.size());

        assertEquals("instructor1@emailprovider.ca", instructors.get(0).getEmail());
        assertEquals("instructor1Password", instructors.get(0).getPassword());

        assertEquals("instructor2@emailprovider.ca", instructors.get(1).getEmail());
        assertEquals("instructor2Password", instructors.get(1).getPassword());
    }
}
