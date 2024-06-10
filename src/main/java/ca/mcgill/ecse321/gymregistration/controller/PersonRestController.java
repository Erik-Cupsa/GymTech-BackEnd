package ca.mcgill.ecse321.gymregistration.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.gymregistration.dto.PersonDto;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.PersonService;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;


@CrossOrigin(origins="*")
@RestController
public class PersonRestController {
    
    @Autowired
    PersonService personService;

    /**
     * getAllPersons: getting all persons
     * @return All persons in database
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/persons", "/persons/"})
    public List<PersonDto> getAllPersons() throws IllegalArgumentException {
        return personService.getAllPersons().stream().map(PersonDto::new).collect(Collectors.toList());
    }

    /**
     * getPersonsByName: getting all persons by name
     * @param name : Name to search
     * @return Persons with passed name in the system
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/persons/byname/{name}", "/persons/byname/{name}/"})
    public List<PersonDto> getPersonsByName(@PathVariable("name") String name) throws IllegalArgumentException {
        return personService.getPersonsByName(name).stream().map(PersonDto::new).collect(Collectors.toList());
    }

    /**
     * getPersonById: getting a person by id
     * @param id : Id of person to search
     * @return Person in the system
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/persons/{id}", "/persons/{id}/"})
    public ResponseEntity<PersonDto> getPersonById(@PathVariable("id") int id) throws IllegalArgumentException {
        Person person = personService.getPersonById(id);
        
        return new ResponseEntity<PersonDto>(new PersonDto(person), HttpStatus.OK);
    }

    /**
     * createPerson: creating a person
     * @return Person in the system
     * @throws IllegalArgumentException
     */
    @PostMapping(value = {"/persons/create", "persons/create/"})
    public ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto) throws IllegalArgumentException {
        try{
        Person person = personService.createPerson(personDto.getName());
        return new ResponseEntity<>(new PersonDto(person), HttpStatus.CREATED);
        } catch (GRSException e){
            return new ResponseEntity<>(new PersonDto(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * updatePerson: updating an existing person
     * @param id : Id of person to update
     * @param personDto : person dto of person to update
     * @return The updated person
     * @throws IllegalArgumentException
     */
    @PutMapping(value = {"/persons-update/{id}", "/persons-update/{id}/"})
    public ResponseEntity<PersonDto>  updatePerson(@PathVariable("id") int id, @RequestBody PersonDto personDto) throws IllegalArgumentException {
        Person toUpdate = personService.getPersonById(id);
        Person person = personService.updateName(id, toUpdate.getName(), personDto.getName());
        return new ResponseEntity<PersonDto>(new PersonDto(person), HttpStatus.OK);
    }

    /**
     * deletePerson: deleting a person by id
     * @param id : Id of person to delete
     * @throws IllegalArgumentException
     */
    @DeleteMapping(value = {"/persons/delete/{id}", "/perons/delete/{id}/"})
    public void deletePerson(@PathVariable("id") int id) throws IllegalArgumentException {
        personService.deletePerson(id);
    }
}
