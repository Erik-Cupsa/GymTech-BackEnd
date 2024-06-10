package ca.mcgill.ecse321.gymregistration.dao;

import ca.mcgill.ecse321.gymregistration.model.Instructor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InstructorRepository extends CrudRepository<Instructor, Integer> {
    Instructor findInstructorById(int id);
    List<Instructor> findInstructorsByPerson_Name(String name);
    Instructor findInstructorByEmail(String email);
    List<Instructor> findAll();
    Instructor findInstructorByEmailAndPassword(String email, String password);
    void deleteInstructorByEmail(String email);
}
