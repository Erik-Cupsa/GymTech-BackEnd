package ca.mcgill.ecse321.gymregistration.dao;

import ca.mcgill.ecse321.gymregistration.model.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Integer>{
    Customer findCustomerById(Integer id);
    List<Customer> findCustomersByPerson_Name(String name);

    Customer findCustomerByEmail(String email);

    List<Customer> findAll();

    void deleteCustomerByEmail(String email);

    Customer findCustomerByEmailAndPassword(String email, String password);
}
