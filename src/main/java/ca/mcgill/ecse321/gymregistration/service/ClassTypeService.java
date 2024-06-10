package ca.mcgill.ecse321.gymregistration.service;

import ca.mcgill.ecse321.gymregistration.dao.ClassTypeRepository;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassTypeService {

    @Autowired
    ClassTypeRepository classTypeRepository;

    private static final int MAX_CLASS_TYPES = 100;

    /**
     * CreateClassType: service method to create and store a class type in the database
     * @param name: name of class type
     * @param isApproved: approval of class type
     * @param gymUser: user creating the class type
     * @return created class type
     * @throws GRSException if attempt to create class type is invalid
     */
    @Transactional
    public ClassType createClassType(String name, boolean isApproved, GymUser gymUser){
        if(!(gymUser instanceof Owner)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Only owners can create class types.");}
        if(name == null || name.trim().isEmpty()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Name cannot be empty.");
        }
        if(!isApproved){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Class Type must be approved.");
        }
        if (classTypeRepository.findAll().size() >= MAX_CLASS_TYPES) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "Maximum number of class types reached.");
        }
        if(classTypeRepository.findClassTypeByName(name) != null){
            throw new GRSException(HttpStatus.CONFLICT, "Class Type " + name + " already exists.");
        }
        ClassType classType = new ClassType();
        classType.setName(name);
        classType.setApproved(isApproved);
        return classTypeRepository.save(classType);
    }

    /**
     * UpdateClassType: service method to update class type and store in database
     * @param oldName: name of old class type
     * @param newName: name of new class type
     * @param isApproved: is approved of new class type
     * @param gymUser: user updating the class type
     * @return Updated ClassType
     * @throws GRSException if attempt to update class type is invalid
     */
    @Transactional
    public ClassType updateClassType(String oldName, String newName, boolean isApproved, GymUser gymUser){
        if(!(gymUser instanceof Owner)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Only owners can update class types.");
        }
        if(newName == null || newName.trim().isEmpty()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Name cannot be empty.");
        }
        if(!isApproved){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Class Type must be approved.");
        }
        if(classTypeRepository.findClassTypeByName(oldName) == null){
            throw new GRSException(HttpStatus.CONFLICT, "Class Type " + oldName + " does not exist.");
        }
        ClassType toUpdate = classTypeRepository.findClassTypeByName(oldName);
        toUpdate.setName(newName);
        toUpdate.setApproved(isApproved);
        return classTypeRepository.save(toUpdate);
    }

    /**
     * GetClassTypeByName: fetch an existing class type with a name
     * @param name: name used to fetch class type
     * @return class type that is found
     * @throws GRSException class type not found
     */
    @Transactional
    public ClassType getClassTypeByName(String name){
        ClassType classType = classTypeRepository.findClassTypeByName(name);
        if (classType == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Class Type not found.");
        }
        return classType;
    }

    /**
     * GetAllClassTypes: getting all existing class types
     * @return list of all existing class types
     * @throws GRSException no class types found
     */
    @Transactional
    public List<ClassType> getAllClassTypes(){
        List<ClassType> classTypes = classTypeRepository.findAll();
        if(classTypes.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No Class Types found in the system.");
        }
        return classTypes;
    }

    /**
     * DeleteClassType: delete the class type
     * @param name: class type to be deleted
     * @param gymUser: user deleting the class type
     * @throws GRSException class type not found
     */
    @Transactional
    public void deleteClassType(String name, GymUser gymUser){
        if(!(gymUser instanceof Owner)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Only owners can delete class types.");
        }
        ClassType classType = classTypeRepository.findClassTypeByName(name);
        if(classType == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Class Type not found.");
        }
        classTypeRepository.deleteClassTypeByName(name);
    }

    /**
     * ProposeClassType: proposing a new class type
     * @param name: name of class type
     * @param gymUser: user proposing the class type
     * @return the class type
     * @throws GRSException invalid request for class type
     */
    @Transactional
    public ClassType proposeClassType(String name, GymUser gymUser) {
        if (!(gymUser instanceof Owner) && !(gymUser instanceof Instructor)) {
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Only owners and instructors can propose class types.");
        }
        if (name == null || name.trim().isEmpty()){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Name cannot be empty.");
        }
        if (classTypeRepository.findAll().size() >= MAX_CLASS_TYPES) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "Maximum number of class types reached.");
        }
        if (classTypeRepository.findClassTypeByName(name) != null) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "Class Type " + name + " already exists.");
        }
        ClassType classType = new ClassType();
        classType.setName(name);
        classType.setApproved(false); // Not approved until owner approves it
        classTypeRepository.save(classType);
        return classType;
    }

    /**
     * ApproveProposedClassType: approving a proposed class type
     * @param name: class type to be approved
     * @param gymUser: user approving the class type
     * @return the approved class type
     * @throws GRSException invalid request for class type approval
     */
    @Transactional
    public ClassType approveProposedClassType(String name, GymUser gymUser){
        if(!(gymUser instanceof Owner)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Only owners can approve class types.");
        }
        ClassType classType = classTypeRepository.findClassTypeByName(name);
        if(classType == null){
            System.out.println("here");
            throw new GRSException(HttpStatus.NOT_FOUND, "Class Type not found.");
        }
        classType.setApproved(true);
        return classTypeRepository.save(classType);
    }

}
