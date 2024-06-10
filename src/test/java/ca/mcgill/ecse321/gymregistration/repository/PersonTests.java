package ca.mcgill.ecse321.gymregistration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Person;

import java.util.List;

@SpringBootTest
public class PersonTests {
    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        personRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadPerson() {
        // Create person.
        String name = "John";
        Person person = new Person(name);

        // Save person to database.
        personRepository.save(person);

        // Read person from database.
        Person personFromDB = personRepository.findPersonById(person.getId());

        // Assert person is not null and has correct attributes.
        assertNotNull(personFromDB);
        
        assertEquals(person.getId(), personFromDB.getId());
        assertEquals(name, personFromDB.getName());
    }
    @Test
    public void testFindPersonByName() {
        // Create persons with different names.
        Person john = new Person("John");
        Person jane = new Person("Jane");
        Person jim = new Person("Jim");
        // Save persons to the database.
        personRepository.save(john);
        personRepository.save(jane);
        personRepository.save(jim);
        // Search for persons by name.
        List<Person> foundPersons = personRepository.findPersonsByName("John");
        // Assert the list is not null and contains the correct number of persons.
        assertNotNull(foundPersons);
        assertEquals(1, foundPersons.size());
        // Assert the attributes of the found person.
        Person foundPerson = foundPersons.get(0);
        assertEquals(john.getId(), foundPerson.getId());
        assertEquals("John", foundPerson.getName());
    }
}
