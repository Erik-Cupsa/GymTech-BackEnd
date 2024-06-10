package ca.mcgill.ecse321.gymregistration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;

import java.util.List;

@SpringBootTest
public class OwnerTests {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        ownerRepository.deleteAll();
        personRepository.deleteAll();
    }
    @Test
    public void testCreateAndReadOwner() {
        // Create and persist person.
        Person john = createAndPersistPerson("John");

        // Create owner.
        Owner owner = createAndPersistOwner("myemail@emailprovider.com", "password", john);

        // Read owner from database.
        Owner ownerFromDB = ownerRepository.findOwnerById(owner.getId());

        // Assertions
        assertOwnerAttributes(owner, ownerFromDB);
        assertPersonAttributes(john, ownerFromDB.getPerson());
    }

    @Test
    public void testFindOwnersByPersonName() {
        // Create and persist person.
        Person john = createAndPersistPerson("John Doe");

        // Create owners with the same person.
        createAndPersistOwner("owner1@emailprovider.ca", "owner1Password", john);
        createAndPersistOwner("owner2@emailprovider.ca", "owner2Password", john);

        // Find owners by person name.
        List<Owner> owners = ownerRepository.findOwnersByPerson_Name(john.getName());

        // Assertions
        assertOwnersListAttributes(owners);
    }

    private Person createAndPersistPerson(String name) {
        Person person = new Person(name);
        return personRepository.save(person);
    }

    private Owner createAndPersistOwner(String email, String password, Person person) {
        Owner owner = new Owner(email, password, person);
        return ownerRepository.save(owner);
    }

    private void assertOwnerAttributes(Owner expected, Owner actual) {
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

    private void assertOwnersListAttributes(List<Owner> owners) {
        assertNotNull(owners);
        assertEquals(2, owners.size());

        assertEquals("owner1@emailprovider.ca", owners.get(0).getEmail());
        assertEquals("owner1Password", owners.get(0).getPassword());

        assertEquals("owner2@emailprovider.ca", owners.get(1).getEmail());
        assertEquals("owner2Password", owners.get(1).getPassword());
    }
}
