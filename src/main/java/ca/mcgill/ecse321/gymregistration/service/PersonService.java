package ca.mcgill.ecse321.gymregistration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import jakarta.transaction.Transactional;

@Service
public class PersonService {
    
    @Autowired
    PersonRepository personRepository;

    /**
     * CreatePerson: create a new person
     * @param name: name of the person
     * @return the new person
     * @throws GRSException if no name provided
     */
    @Transactional 
    public Person createPerson(String name){
        if(name == null || name.trim().isEmpty()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Must include name.");
        }
        
        Person person = new Person();
        person.setName(name);
        personRepository.save(person);
        return person;        
    }

    /**
     * UpdateName: allows users to update their name
     * @param oldName: the old name
     * @param newName: the new name
     * @return the person
     * @throws GRSException if invalid update request
     */
    @Transactional
    public Person updateName(int id, String oldName, String newName){
        Person person = personRepository.findPersonById(id);
        if(person == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Person not found.");
        }
        if(!(oldName.equals(person.getName()))){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Invalid old name.");
        }
        if(newName == null || newName.trim().isEmpty()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Invalid name.");
        }
        person.setName(newName);
        return person;
    }


    /**
     * GetPersonById: get a person by their id
     * @param id: the id of the person
     * @return the person
     * @throws GRSException if invalid creation request
     */
    @Transactional
    public Person getPersonById(int id){
        Person person = personRepository.findPersonById(id);
        if(person == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Person not found.");
        }
        return person;
    }

    /**
     * GetPersonsByName: get all persons by a name
     * @param name: the name to search for
     * @return list of persons with that name
     * @throws GRSException if invalid creation request
     */
    @Transactional
    public List<Person> getPersonsByName(String name){
        if(name == null || name.trim().isEmpty()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Must include name.");
        }
        List<Person> persons = personRepository.findPersonsByName(name);
        if(persons.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No persons with this name found in the system.");
        }
        return persons;
    }

    /**
     * GetAllPersons: get list of all persons
     * @return list of all persons
     * @throws GRSException if no persons in the database
     */
    @Transactional
    public List<Person> getAllPersons(){
        List<Person> allPersons = (List<Person>) personRepository.findAll();
        if(allPersons.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No persons found in the system.");
        }
        return allPersons;
    }

    /**
     * DeletePerson: deletes a person by id
     * @param id: the id of the person   
     * @throws GRSException if person not in database
     */
    @Transactional
    public void deletePerson(int id){
        Person person = personRepository.findPersonById(id);
        if(person == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Person not found.");
        }
        personRepository.deletePersonById(id);
    }

}
