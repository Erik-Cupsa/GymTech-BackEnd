package ca.mcgill.ecse321.gymregistration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@ExtendWith(MockitoExtension.class)
public class TestPersonService {
    
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private static final String NAME = "Joe Biden";
    private static final int ID = 0;
    private final Person PERSON = new Person();

    @BeforeEach
    public void setMockOutput(){
        lenient().when(personRepository.findPersonById(anyInt())).thenAnswer((InvocationOnMock invocation) -> {
            int id = invocation.getArgument(0);
            if(id == ID){
                Person person = new Person();
                person.setName(NAME);
                person.setId(ID);
                return person;
            }else{
                return null;
            }
        });

        lenient().when(personRepository.findAll()).thenAnswer((InvocationOnMock invocation) -> {
            PERSON.setId(ID);
            PERSON.setName(NAME);

            List<Person> personList = new ArrayList<>();
            personList.add(PERSON);
            return personList;
        });

        lenient().when(personRepository.findPersonsByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            if (invocation.getArgument(0).equals(NAME)){
                Person person = new Person();
                person.setId(ID);
                person.setName(NAME);

                List<Person> personsList = new ArrayList<>();
                personsList.add(person);
                return personsList;
            }else{
                return null;
            }
        });
    }

    @Test
    public void testCreatePerson(){
        String name = "SpongeBob SquarePants";

        Person person = null;
        try{
            person = personService.createPerson(name);
        }catch (GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(person);
        assertEquals(name, person.getName());
    }

    @Test
    public void testCreatePersonNullName(){
        String name = null;

        Person person = null;
        try{
            person = personService.createPerson(name);
            fail();
        }catch (GRSException e){
            assertEquals("Must include name.", e.getMessage());
        }
        assertNull(person);
    }

    @Test
    public void testCreatePersonEmptryName(){
        String name = "";

        Person person = null;
        try{
            person = personService.createPerson(name);
            fail();
        }catch (GRSException e){
            assertEquals("Must include name.", e.getMessage());
        }
        assertNull(person);
    }

    @Test
    public void updatePerson(){
        int id = 0;
        String oldName = "Joe Biden";
        String newName = "SpongeBob SquarePants";

        Person person = null;
        try{
            person = personService.updateName(id, oldName, newName);
        }catch (GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(person);
        assertEquals(newName, person.getName());
    }

    @Test
    public void updatePersonInvalidId(){
        int id = 1;
        String oldName = "Joe Biden";
        String newName = "SpongeBob SquarePants";

        Person person = null;
        try{
            person = personService.updateName(id, oldName, newName);
            fail();
        }catch (GRSException e){
            assertEquals("Person not found.", e.getMessage());
        }
        assertNull(person);
    }
    @Test
    public void updatePersonInvalidName(){
        int id = 0;
        String oldName = "Joe";
        String newName = "SpongeBob SquarePants";

        Person person = null;
        try{
            person = personService.updateName(id, oldName, newName);
            fail();
        }catch (GRSException e){
            assertEquals("Invalid old name.", e.getMessage());
        }
        assertNull(person);
    }


    @Test 
    public void updatePersonToNullName(){
        int id = 0;
        String oldName = "Joe Biden";
        String newName = null;

        Person person = null;
        try{
            person = personService.updateName(id, oldName, newName);
            fail();
        }catch (GRSException e){
            assertEquals("Invalid name.", e.getMessage());
        }
        assertNull(person);
    }

    @Test 
    public void updatePersonToEmptyName(){
        int id = 0;
        String oldName = "Joe Biden";
        String newName = "";

        Person person = null;
        try{
            person = personService.updateName(id, oldName, newName);
            fail();
        }catch (GRSException e){
            assertEquals("Invalid name.", e.getMessage());
        }
        assertNull(person);
    }

    @Test
    public void getPerson(){
        Person person = null;
        try{
            person = personService.getPersonById(ID);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertNotNull(person);
        assertEquals(ID, person.getId());
        assertEquals(NAME, person.getName());
    }

    @Test 
    public void testGetPersonInvalidId(){
        Person person = null;
        try{
            person = personService.getPersonById(1);
            fail();
        }catch (GRSException e){
            assertEquals("Person not found.", e.getMessage());
        }
        assertNull(person);
    }

    @Test 
    public void testGetPersonsWithNameOne(){
        String name = "Joe Biden";
        List<Person> persons = new ArrayList<>();

        try{
            persons = personService.getPersonsByName(name);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains(name));
    }

    @Test
    public void testGetPersonWithNameMultiple(){
        String searchedName = "Michel Tremblay";
        String otherName = "Jean Bouchard";
        List<Person> addedPersons = new ArrayList<>();
        for(int i = 0; i<3; i++){
            Person addedPerson = new Person();
            addedPerson.setName(searchedName);
            addedPerson.setId(i*2);
            addedPersons.add(addedPerson);
        }
        for(int i = 1; i<4; i++){
            Person addedPerson = new Person();
            addedPerson.setName(otherName);
            addedPerson.setId(i*2-1);
            addedPersons.add(addedPerson);
        }
        when(personRepository.findPersonsByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
            List<Person> findPersons = new ArrayList<>();

            for(int i=0; i<addedPersons.size(); i++){
                Person person = addedPersons.get(i);
                if(person.getName().equals(invocation.getArgument(0))){
                    findPersons.add(person);
                }
            }
            return findPersons;
        });

        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getPersonsByName(searchedName);
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains(searchedName));
        assertTrue(persons.stream().map(Person::getId).collect(Collectors.toList()).contains(0));
        assertTrue(persons.stream().map(Person::getId).collect(Collectors.toList()).contains(2));
        assertTrue(persons.stream().map(Person::getId).collect(Collectors.toList()).contains(4));

        assertFalse(persons.stream().map(Person::getName).collect(Collectors.toList()).contains(otherName));
        assertFalse(persons.stream().map(Person::getId).collect(Collectors.toList()).contains(1));
        assertFalse(persons.stream().map(Person::getId).collect(Collectors.toList()).contains(3));
        assertFalse(persons.stream().map(Person::getId).collect(Collectors.toList()).contains(5));
    }

    @Test
    public void testGetPersonWithNameNone(){
        List<Person> addedPersons = new ArrayList<>();
        when(personRepository.findPersonsByName(anyString())).thenReturn(addedPersons);

        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getPersonsByName(NAME);
            fail();
        }catch (GRSException e){
            assertEquals("No persons with this name found in the system.", e.getMessage());
        }
        assertEquals(0, persons.size());
    }

    @Test 
    public void testGetPersonWithNullName(){
        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getPersonsByName(null);
            fail();
        }catch (GRSException e){
            assertEquals("Must include name.", e.getMessage());
        }
        assertEquals(0, persons.size());
    }

    @Test 
    public void testGetPersonWithEmptyName(){
        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getPersonsByName("");
            fail();
        }catch (GRSException e){
            assertEquals("Must include name.", e.getMessage());
        }
        assertEquals(0, persons.size());
    }

    @Test
    public void testGetAllPersonsOne(){
        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getAllPersons();
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains("Joe Biden"));
    }

    @Test void testGetAllPersonsMultiple(){
        List<Person> addedPersons = new ArrayList<>();
        for (int i=0; i<5; i++){
            Person addedPerson = new Person();
            addedPerson.setId(i);
            addedPerson.setName("Name "+i);
            addedPersons.add(addedPerson);
        }
        when(personRepository.findAll()).thenReturn(addedPersons);

        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getAllPersons();
        }catch(GRSException e){
            fail(e.getMessage());
        }
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains("Name 0"));
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains("Name 1"));
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains("Name 2"));
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains("Name 3"));
        assertTrue(persons.stream().map(Person::getName).collect(Collectors.toList()).contains("Name 4"));
    }

    @Test
    public void testGetAllPersonsNone(){
        List<Person> addedPersons = new ArrayList<>();
        when(personRepository.findAll()).thenReturn(addedPersons);

        List<Person> persons = new ArrayList<>();
        try{
            persons = personService.getAllPersons();
            fail();
        }catch (GRSException e){
            assertEquals("No persons found in the system.", e.getMessage());
        }
        assertEquals(0, persons.size());
    }

    @Test
    public void testDeletePerson(){
        int id = 0;
        try{
            personService.deletePerson(id);
        }catch(Exception e){
            fail(e.getMessage());
        }
        verify(personRepository).deletePersonById(id);
    }

    @Test 
    public void testDeletePersonDoesNotExist(){
        int id = 1;
        try{
            personService.deletePerson(id);
            fail();
        }catch(GRSException e){
            assertEquals("Person not found.", e.getMessage());
        }
    }
}

