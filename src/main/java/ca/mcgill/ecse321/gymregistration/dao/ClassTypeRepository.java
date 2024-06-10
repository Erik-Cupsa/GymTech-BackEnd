package ca.mcgill.ecse321.gymregistration.dao;

import ca.mcgill.ecse321.gymregistration.model.ClassType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClassTypeRepository extends CrudRepository<ClassType, Integer> {
    ClassType findClassTypeById(int id);

    ClassType findClassTypeByName(String name);
    List<ClassType> findAll();

    void deleteClassTypeByName(String name);
}
