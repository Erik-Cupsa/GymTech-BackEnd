package ca.mcgill.ecse321.gymregistration.controller;

import ca.mcgill.ecse321.gymregistration.dto.InstructorRegistrationDto;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.InstructorRegistration;
import ca.mcgill.ecse321.gymregistration.service.InstructorRegistrationService;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class InstructorRegistrationRestController {
    @Autowired
    private InstructorRegistrationService instructorRegistrationService;

    /**
     * RegisterInstructorForClass: register an instructor for a class
     * 
     * @param instructorRegistrationDto: the instructor registration dto
     * @return the response entity of the result
     */
    @PostMapping(value = { "/instructor-registration/create", "/instructor-registration/create/" })
    public ResponseEntity<InstructorRegistrationDto> registerInstructorForClass(
            @RequestBody InstructorRegistrationDto instructorRegistrationDto) {
        try {
                InstructorRegistration instructorRegistration = instructorRegistrationService.registerInstructorForClass(
                    instructorRegistrationDto.getSession().getId(),
                    instructorRegistrationDto.getInstructor().getEmail(), instructorRegistrationDto.getInstructor());
            return new ResponseEntity<>(new InstructorRegistrationDto(instructorRegistration),
                    HttpStatus.CREATED);
        } catch (GRSException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new InstructorRegistrationDto(), e.getStatus());
        }
    }

    /**
     * RemoveInstructorFromClass: remove an instructor from a class
     * 
     * @param id: the id of the instructor registration
     * @throws IllegalArgumentException
     * 
     */
    @DeleteMapping(value = { "/instructor-registration/delete/{id}", "/instructor-registration/delete/{id}/" })
    public void removeInstructorFromClass(@PathVariable("id") int id, @RequestBody String email,
            @RequestBody GymUser gymUser) throws IllegalArgumentException {
        instructorRegistrationService.removeInstructorFromClass(id, email, gymUser);
    }

    /**
     * UpdateInstructorRegistration: update instructor registration
     * 
     * @param id:                        the id of the instructor registration
     * @param instructorRegistrationDto: the instructor registration dto
     * @return A response entity containing the updated Dto and the status
     * @throws IllegalArgumentException
     */
    @PutMapping(value = { "/instructor-registration/{id}", "/instructor-registration/{id}/" })
    public ResponseEntity<InstructorRegistrationDto> updateInstructorRegistration(@PathVariable("id") int id,
            @RequestBody InstructorRegistrationDto instructorRegistrationDto) throws IllegalArgumentException {
        InstructorRegistration instructorRegistration = instructorRegistrationService.updateInstructorRegistration(id,
                instructorRegistrationDto);
        return new ResponseEntity<>(new InstructorRegistrationDto(instructorRegistration),
                HttpStatus.OK);
    }

    /**
     * GetInstructorRegistration: get instructor registration by id
     * 
     * @param id: the id of the instructor registration
     * @return a response entity containing the Dto and the status
     */
    @GetMapping(value = { "/instructor-registration-s/{id}", "/instructor-registration-s/{id}" })
    public List<InstructorRegistration> getInstructorRegistration(@PathVariable("id") int id) {
        return instructorRegistrationService.getInstructorRegistrationBySession(id).stream()
                .map(InstructorRegistration::new).collect(Collectors.toList());
    }

    /**
     * GetInstructorRegistrationsByInstructor: get instructor registrations by
     * instructor
     * 
     * @param email: the email of the instructor
     * @return a list of instructor registrations
     */
    @GetMapping(value = { "/instructor-registration-i/{email}", "/instructor-registration-i/{email}/" })
    public List<InstructorRegistration> getInstructorRegistrationsByInstructor(@PathVariable("email") String email) {
        return instructorRegistrationService.getInstructorRegistrationsByInstructor(email).stream()
                .map(InstructorRegistration::new).collect(Collectors.toList());
    }

    /**
     * GetInstructorRegistrationByInstructorAndSession: get instructor registration
     * by instructor and session
     * 
     * @param sessionId: the id of the session
     * @param email:     the email of the instructor
     * @return a response entity containing the Dto and the status
     */
    @GetMapping(value = { "/instructor-registration/{sessionId}/{email}",
            "/instructor-registration/{sessionId}/{email}/" })
    public ResponseEntity<InstructorRegistrationDto> getInstructorRegistrationByInstructorAndSession(
            @PathVariable("sessionId") int sessionId, @PathVariable("email") String email) {
        InstructorRegistration instructorRegistration = instructorRegistrationService
                .getInstructorRegistrationByInstructorAndSession(sessionId, email);
        return new ResponseEntity<>(new InstructorRegistrationDto(instructorRegistration), HttpStatus.OK);
    }
}