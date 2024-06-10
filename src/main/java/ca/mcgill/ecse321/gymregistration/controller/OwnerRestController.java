package ca.mcgill.ecse321.gymregistration.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse321.gymregistration.dto.OwnerDto;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.service.OwnerService;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;


@CrossOrigin(origins="*")
@RestController
public class OwnerRestController {
    @Autowired
    private OwnerService ownerService;

    /**
     * GetOwner: getting owner by email
     * @param email: Email of owner
     * @return Owner in database
     * @throws IllegalArgumentException
     */
     @GetMapping(value ={"/owners/{email}", "/owners/{email}/"})
     public ResponseEntity<OwnerDto> getOwner(@PathVariable("email") String email) throws IllegalArgumentException {
        Owner owner = ownerService.getOwnerByEmail(email);
        return new ResponseEntity<>(new OwnerDto(owner), HttpStatus.OK);
    }

    /**
     * GetAllOwners: getting all owners by email
     * @return All owners in database
     */
    @GetMapping(value = { "/owners", "/owners/"})
    public List<OwnerDto> getAllOwners() {
        return ownerService.getAllOwners().stream().map(OwnerDto::new).collect(Collectors.toList());
    }

    /**
     * CreateOwner: creating a owner
     * @param ownerDto : onwer dto to be created
     * @return Owner in system
     * @throws IllegalArgumentException
     */
    @PostMapping(value = { "/owners/create", "/owners/create/"})
    public ResponseEntity<OwnerDto> createOwner(@RequestBody OwnerDto ownerDto) throws IllegalArgumentException{
        try{
        Owner owner = ownerService.createOwner(ownerDto.getEmail(), ownerDto.getPassword(), ownerDto.getPerson().getId());
        return new ResponseEntity<>(new OwnerDto(owner), HttpStatus.CREATED);
        }
        catch(GRSException e)
        {
            return new ResponseEntity<>(new OwnerDto(), e.getStatus());
        }
    }

    /**
     * UpdateOwner: updating an existing owner
     * @param email: name of owner to update
     * @param ownerDto : owner dto to be updated
     * @return The updated owner
     * @throws IllegalArgumentException
     */
    @PutMapping(value = {"/owners/update-owners-e/{email}", "/owners/update-owners-e/{email}/"})
    public ResponseEntity<OwnerDto> updateOwner(@PathVariable("email") String email, @RequestBody OwnerDto ownerDto) throws IllegalArgumentException{
       
        Owner toUpdate = ownerService.getOwnerByEmail(ownerDto.getEmail());
        Owner owner = ownerService.updateEmail(toUpdate.getEmail(), ownerDto.getPassword(), email);
        owner = ownerService.updatePassword(owner.getEmail(), toUpdate.getPassword(), ownerDto.getPassword());

        return new ResponseEntity<>(new OwnerDto(owner), HttpStatus.OK);
    }

    /**
     * DeleteOwner: deleting an owner from the system
     * @param email: Email of owner to be deleted
     * @param gymUser: the user trying to delete the owner
     * @throws IllegalArgumentException
     */
    @DeleteMapping(value = {"/owners/delete/{email}", "/owners/delete/{email}/"})
    public void deleteInstructor(@PathVariable("email") String email, @RequestBody GymUser gymUser) throws IllegalArgumentException{
        ownerService.deleteOwner(email, gymUser);
    }

    /**
     * LoginOwner: allow an owner to log in
     * @param email: Email of the owner
     * @param password: password of the owner
     * @return The owner
     * @throws GRSException Invalid owner email or password
     */
    @GetMapping(value = {"/owners/login/{email}/{password}","/owners/login/{email}/{password}/"})
    public ResponseEntity<OwnerDto> logInOwner(@PathVariable("email") String email,@PathVariable("password") String password ) throws IllegalArgumentException{
        Owner owner;
        try {
            owner = ownerService.loginOwner(email, password);
            return new ResponseEntity<>(new OwnerDto(owner), HttpStatus.OK);
        } catch (GRSException e) {
            return new ResponseEntity<>(new OwnerDto(), e.getStatus());
        }
    }
}
