package ca.mcgill.ecse321.gymregistration.controller;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dto.ClassTypeDto;
import ca.mcgill.ecse321.gymregistration.dto.GymUserDto;
import ca.mcgill.ecse321.gymregistration.model.*;
import ca.mcgill.ecse321.gymregistration.service.ClassTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins="*")
@RestController
public class ClassTypeRestController {
    @Autowired
    private ClassTypeService classTypeService;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * GetAllClassTypes: getting all class types
     * @return All class types in database
     */
    @GetMapping(value = { "/class-types", "/class-types/"})
    public List<ClassTypeDto> getAllClassTypes() {
        return classTypeService.getAllClassTypes().stream().map(ClassTypeDto::new).collect(Collectors.toList());
    }

    /**
     * GetClassType: getting a class type by name
     * @param name: Name of class type to get
     * @return ClassType in system
     * @throws IllegalArgumentException
     */
    @GetMapping(value = {"/class-types/{name}", "/class-types/{name}/"})
    public ResponseEntity<ClassTypeDto> getClassType(@PathVariable("name") String name) throws IllegalArgumentException {
        ClassType classType = classTypeService.getClassTypeByName(name);
        return new ResponseEntity<>(new ClassTypeDto(classType), HttpStatus.OK);
    }
    /**
     * CreateClassType: creating a class type
     * @param email: email of user creating the class type
     * @param classTypeDto : class type dto to be created
     * @return Class type in system
     * @throws IllegalArgumentException
     */
    @PostMapping(value = { "/class-types/create/{email}", "/class-types/create/{email}/"})
    public ResponseEntity<ClassTypeDto> createClassType(@PathVariable("email") String email, @RequestBody ClassTypeDto classTypeDto) throws IllegalArgumentException{
        Owner owner = ownerRepository.findOwnerByEmail(email);
        ClassType classType = classTypeService.createClassType(classTypeDto.getName(), classTypeDto.isApproved(), owner);
        return new ResponseEntity<>(new ClassTypeDto(classType), HttpStatus.CREATED);
    }

    /**
     * ProposeClassType: proposing a class type
     * @param name: name of class type
     * @param gymUserdto: user proposing the class type
     * @return Proposed class type
     * @throws IllegalArgumentException
     */
    @PostMapping(value = { "/class-types/propose/{name}", "/class-types/propose/{name}/"})
    public ResponseEntity<ClassTypeDto> proposeClassType(@PathVariable("name") String name, @RequestBody GymUserDto gymUserdto) throws IllegalArgumentException{
        ClassType classType;
        if(instructorRepository.findInstructorByEmail(gymUserdto.getEmail())!= null){
            Instructor instructor = instructorRepository.findInstructorByEmail(gymUserdto.getEmail());
            classType = classTypeService.proposeClassType(name, instructor);
        }
        else if(ownerRepository.findOwnerByEmail(gymUserdto.getEmail())!= null) {
            Owner owner = ownerRepository.findOwnerByEmail(gymUserdto.getEmail());
            classType = classTypeService.proposeClassType(name, owner);
        }
        else if(customerRepository.findCustomerByEmail((gymUserdto.getEmail())) != null) {
            Customer customer = customerRepository.findCustomerByEmail(gymUserdto.getEmail());
            classType = classTypeService.proposeClassType(name, customer);
        }
        else {
            Customer customer = new Customer(gymUserdto.getEmail(), gymUserdto.getPassword(), gymUserdto.getPerson());
            classType = classTypeService.proposeClassType(name, customer);
        }
        return new ResponseEntity<>(new ClassTypeDto(classType), HttpStatus.CREATED);
    }

    /**
     * UpdateClassType: updating an existing class type
     * @param name: name of class type to update
     * @param classTypeDto : class type dto to be updated
     * @param email: email of the user updating the class type
     * @return The updated class type
     * @throws IllegalArgumentException
     */
    @PutMapping(value = {"/class-types/{name}/email/{email}", "/class-types/{name}/email/{email}/"})
    public ResponseEntity<ClassTypeDto> updateClassType(
            @PathVariable("name") String name,
            @RequestBody ClassTypeDto classTypeDto,
            @PathVariable("email") String email) throws IllegalArgumentException {

        Owner owner = ownerRepository.findOwnerByEmail(email); // Adjust this according to your GymUser constructor
        ClassType toUpdate = classTypeService.getClassTypeByName(name);
        ClassType classType = classTypeService.updateClassType(toUpdate.getName(), classTypeDto.getName(), classTypeDto.isApproved(), owner);
        return new ResponseEntity<>(new ClassTypeDto(classType), HttpStatus.OK);
    }

    /**
     * ApproveProposedClassType: approving the proposed class type
     * @param name: name of class type
     * @param gymUserDto: user approving the class type
     * @return Approved class type
     * @throws IllegalArgumentException
     */
    @PutMapping(value = {"/class-types/approve/{name}", "/class-types/approve/{name}/"})
    public ResponseEntity<ClassTypeDto> approveProposedClassType(@PathVariable("name") String name, @RequestBody GymUserDto gymUserDto) throws IllegalArgumentException{
        GymUser gymUser = ownerRepository.findOwnerByEmail(gymUserDto.getEmail());
        ClassType classType = classTypeService.approveProposedClassType(name, gymUser);
        return new ResponseEntity<>(new ClassTypeDto(classType), HttpStatus.OK);
    }

    /**
     * DeleteClassType: deleting a class type from the system
     * @param name: Name of class type to be deleted
     * @param gymUser: user deleting the class type
     * @throws IllegalArgumentException
     */
    /**
     * DeleteClassType: deleting a class type from the system
     * @param name: Name of class type to be deleted
     * @param gymUserDto: user deleting the class type
     * @throws IllegalArgumentException
     */
    @DeleteMapping(value = {"/class-types/delete/{name}", "/class-types/delete/{name}/"})
    public void deleteClassType(@PathVariable("name") String name, @RequestBody GymUserDto gymUserDto) throws IllegalArgumentException{
        GymUser gymUser = ownerRepository.findOwnerByEmail(gymUserDto.getEmail());
        classTypeService.deleteClassType(name, gymUser);
    }
}
