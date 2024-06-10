package ca.mcgill.ecse321.gymregistration.dao;

import ca.mcgill.ecse321.gymregistration.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Integer> {
    Person findPersonById(Integer id);

    List<Person> findPersonsByName(String name);

    void deletePersonById(Integer id);
}
