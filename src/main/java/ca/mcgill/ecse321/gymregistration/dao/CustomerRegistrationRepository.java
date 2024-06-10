package ca.mcgill.ecse321.gymregistration.dao;

import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.CustomerRegistration;
import ca.mcgill.ecse321.gymregistration.model.Session;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRegistrationRepository extends CrudRepository<CustomerRegistration, Integer> {
    CustomerRegistration findCustomerRegistrationById(Integer id);
    CustomerRegistration findCustomerRegistrationByCustomerAndSession(Customer customer, Session session);
    CustomerRegistration findCustomerRegistrationByCustomer_EmailAndSession_Id(String email, Integer sessionId);

    List<CustomerRegistration> findCustomerRegistrationsBySession_Id(Integer sessionId);
    List<CustomerRegistration> findCustomerRegistrationsByCustomer_Email(String email);

    void deleteCustomerRegistrationsByCustomer_Email(String email);
    void deleteCustomerRegistrationsBySession_Id(int id);
}
