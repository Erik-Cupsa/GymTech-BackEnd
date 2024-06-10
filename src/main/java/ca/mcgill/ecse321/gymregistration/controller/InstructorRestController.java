
package ca.mcgill.ecse321.gymregistration.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ca.mcgill.ecse321.gymregistration.dto.InstructorDto;
import ca.mcgill.ecse321.gymregistration.dto.CustomerDto;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.service.InstructorService;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

@CrossOrigin(origins = "*")
@RestController
public class InstructorRestController {
    @Autowired
    private InstructorService instructorService;

    /**
     * GetInstructor: getting instructor by email
     * @param email: Email of instructor
     * @return Instructor in database
     * @throws IllegalArgumentException
     */
    @GetMapping(value = { "/instructors/{email}", "/instructors/{email}/" })
    public ResponseEntity<InstructorDto> getInstructor(@PathVariable("email") String email) throws IllegalArgumentException {
        Instructor instructor = instructorService.getInstructorByEmail(email);
        return new ResponseEntity<>(new InstructorDto(instructor), HttpStatus.OK);

    }

    /**
     * GetAllInstructors: getting all instructors by email
     * @return All instructors in database
     */
    @GetMapping(value = { "/instructors", "/instructors/" })
    public List<InstructorDto> getAllInstructors() {
        return instructorService.getAllInstructors().stream().map(InstructorDto::new).collect(Collectors.toList());
    }

    /**
     * CreateInstructor: creating an instructor
     * @param instructorDto : instructor dto to be created
     * @return Instructor in system
     * @throws IllegalArgumentException
     */
    @PostMapping(value = { "/instructors/create", "/instructors/create/" })
    public ResponseEntity<InstructorDto> createInstructor(@RequestBody InstructorDto instructorDto) throws IllegalArgumentException {
        try {
        Instructor instructor = instructorService.createInstructor(instructorDto.getEmail(), instructorDto.getPassword(), instructorDto.getPerson().getId());
        return new ResponseEntity<>(new InstructorDto(instructor), HttpStatus.CREATED);
        }
        catch (GRSException e)
        {
            return new ResponseEntity<>(new InstructorDto(), e.getStatus());
        }
    }

    /**
     * UpdateInstructor: updating an existing instructor
     * @param email: name of instructor to update
     * @param instructorDto : instructor dto to be updated
     * @return The updated instructor
     * @throws IllegalArgumentException
     */
    @PutMapping(value = { "/instructors/update-instructors-e/{email}", "/instructors/update-instructors-e/{email}" })
    public ResponseEntity<InstructorDto> updateInstructor(@PathVariable("email") String email, @RequestBody InstructorDto instructorDto) throws IllegalArgumentException {
    
        Instructor toUpdate = instructorService.getInstructorByEmail(instructorDto.getEmail());  
        Instructor instructor = instructorService.updateEmail(toUpdate.getEmail(), instructorDto.getPassword(), email);
        instructor = instructorService.updatePassword(instructor.getEmail(), toUpdate.getPassword(), instructorDto.getPassword());
        return new ResponseEntity<>(new InstructorDto(instructor), HttpStatus.OK);
    }

    /**
     * DeleteInstructor: deleting an existing instructor
     * @param email: email of instructor to delete
     * @param gymUser: the user trying to delete the instructor
     * @throws IllegalArgumentException
     */
    @DeleteMapping(value = { "/instructors/delete/{email}", "/instructors/delete/{email}/" })
    public void deleteInstructor(@PathVariable("email") String email, @RequestBody GymUser gymUser) throws IllegalArgumentException {
        instructorService.deleteIntructor(email, gymUser);
    }

    /**
     * LogInInstructor: logging in an instructor
     * @param email: email of instructor
     * @param password: password of instructor
     * @return The logged in instructor
     * @throws IllegalArgumentException
     */
    @GetMapping(value = { "/instructors/login/{email}/{password}", "/instructors/login/{email}/{password}/" })
    public ResponseEntity<InstructorDto> logInInstructor(@PathVariable("email") String email, @PathVariable("password") String password) throws IllegalArgumentException {
        Instructor instructor;
        try {
            instructor = instructorService.loginInstructor(email, password);
            return new ResponseEntity<>(new InstructorDto(instructor), HttpStatus.OK);
        } catch (GRSException e) {
            return new ResponseEntity<>(new InstructorDto(), e.getStatus());
        }
    }

    /**
     * ChangeAccountType: changing account type of an instructor
     * @param email: email of instructor
     * @param gymUser: user changing the account type
     * @return The updated instructor
     * @throws IllegalArgumentException
     */
    @PutMapping(value = { "/instructor/change-account-type/{email}", "/instructor/change-account-type/{email}/" })
    public ResponseEntity<CustomerDto> changeAccountType(@PathVariable("email") String email, @RequestBody GymUser gymUser) throws IllegalArgumentException {
        Customer customer;
        try {
            customer = instructorService.changeAccountType(email, gymUser);
            return new ResponseEntity<>(new CustomerDto(customer), HttpStatus.OK);
        } catch (GRSException e) {
            return new ResponseEntity<>(new CustomerDto(), e.getStatus());
        }
    }

}
