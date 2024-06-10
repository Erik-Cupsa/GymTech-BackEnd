package ca.mcgill.ecse321.gymregistration.service;

import ca.mcgill.ecse321.gymregistration.dao.CustomerRegistrationRepository;
import ca.mcgill.ecse321.gymregistration.dao.CustomerRepository;
import ca.mcgill.ecse321.gymregistration.dao.InstructorRepository;
import ca.mcgill.ecse321.gymregistration.dao.OwnerRepository;
import ca.mcgill.ecse321.gymregistration.dao.PersonRepository;
import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Person;
import ca.mcgill.ecse321.gymregistration.service.exception.GRSException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    InstructorRepository instructorRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    CustomerRegistrationRepository customerRegistrationRepository;

    /**
     * CreateCustomer: creating a customer
     * @param email: email of the customer
     * @param password: password of the customer
     * @param person_id: id of the person
     * @return the created customer
     * @throws GRSException invalid customer creation request
     */
    @Transactional
    public Customer createCustomer(String email, String password, int person_id){
        if (email == null || password == null || password.trim().isEmpty() || !email.contains("@")) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "Must include valid email and password.");
        }
        if(customerRepository.findCustomerByEmail(email) != null || ownerRepository.findOwnerByEmail(email) != null || instructorRepository.findInstructorByEmail(email) != null){
            throw new GRSException(HttpStatus.CONFLICT, "User with email already exists.");
        }
        Person person = personRepository.findPersonById(person_id);
        if (person == null) {
            throw new GRSException(HttpStatus.NOT_FOUND, "Person not found.");
        }
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setPerson(person);
        customerRepository.save(customer);
        return customer;
    }

    /**
     * UpdateCreditCard: allow users to add or update their credit card information
     * @param email: email of customer
     * @param password: password of customer
     * @param creditCardNumber: credit card number to be added
     * @return the new customer
     * @throws GRSException customer not found, invalid email and password combination
     */
    @Transactional
    public Customer updateCreditCard(String email, String password, String creditCardNumber){
        Customer customer = customerRepository.findCustomerByEmailAndPassword(email, password);
        if (customer == null) {
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found, invalid email and password combination.");
        }
        if(customer.getCreditCardNumber() == creditCardNumber){
            throw new GRSException(HttpStatus.CONFLICT, "New credit card number is same as old one.");
        }
        customer.setCreditCardNumber(creditCardNumber);
        customerRepository.save(customer);
        return customer;
    }

    /**
     * UpdateEmail: allow users to edit their email information
     * @param oldEmail: old email of customer
     * @param password: password of customer
     * @param newEmail: new email of customer
     * @return the new customer
     * @throws GRSException customer not found, invalid email and password combination, or invalid new email
     */
    @Transactional
    public Customer updateEmail(String oldEmail, String password, String newEmail){
        Customer customer = customerRepository.findCustomerByEmailAndPassword(oldEmail, password);
        if (customer == null) {
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found, invalid email and password combination.");
        }
        if(oldEmail.equals(newEmail)){
            throw new GRSException(HttpStatus.CONFLICT, "New email is same as old one.");
        }
        if (newEmail == null || !newEmail.contains("@")) {
            throw new GRSException(HttpStatus.BAD_REQUEST, "Invalid new email.");
        }
        customer.setEmail(newEmail);
        customerRepository.save(customer);
        return customer;
    }

    /**
     * UpdatePassword: allow users to edit their password information
     * @param email: email of customer
     * @param oldPassword: old password of customer
     * @param newPassword: new password of customer
     * @return the new customer
     * @throws GRSException customer not found, invalid email and password combination, or invalid new password
     */
    @Transactional
    public Customer updatePassword(String email, String oldPassword, String newPassword){
        Customer customer = customerRepository.findCustomerByEmailAndPassword(email, oldPassword);
        if (customer == null) {
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found, invalid email and password combination.");
        }
        if(oldPassword.equals(newPassword)){
            throw new GRSException(HttpStatus.CONFLICT, "New password is same as old one.");
        }
        if (newPassword == null){
            throw new GRSException(HttpStatus.BAD_REQUEST, "Invalid new password.");
        }
        customer.setPassword(newPassword);
        customerRepository.save(customer);
        return customer;
    }

    /**
     * GetCustomerByEmail: getting a customer by their email
     * @param email: the email to search with
     * @return the customer
     * @throws GRSException customer not found
     */
    @Transactional
    public Customer getCustomerByEmail(String email){
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null || email.trim().isEmpty()){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        return customer;
    }

    /**
     * GetAllCustomers: getting all existing customers
     * @return list of all existing customers
     * @throws GRSException no customers found
     */
    @Transactional
    public List<Customer> getAllCustomers(){
        List<Customer> customers = customerRepository.findAll();
        if(customers.size() == 0){
            throw new GRSException(HttpStatus.NOT_FOUND, "No Customers found in the system.");
        }
        return customers;
    }

    /**
     * DeleteCustomer: delete the customer
     * @param email: email of customer to be deleted
     * @param gymUser: the user trying to delete the customer
     * @throws GRSException customer not found or the user is not an owner, an instructor, or the customer
     */
    @Transactional
    public void deleteCustomer(String email, GymUser gymUser){
        if((gymUser instanceof Customer) && !gymUser.getEmail().equals(email)){
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Customers can only be deleted by themselves or by instructors and owners.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);
        if(customer == null){
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        customerRepository.deleteCustomerByEmail(email);
    }

    /**
     * LoginCustomer: allow a customer to log in
     * @param email: email of the customer
     * @param password: password of the customer
     * @return the customer
     * @throws GRSException invalid customer email or password
     */
    public Customer loginCustomer(String email, String password){
        Customer customer = customerRepository.findCustomerByEmailAndPassword(email, password);
        if (customer == null) {
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return customer;
    }

    /**
     * ChangeAccountType: allow the owner to change a customer's account type
     * @param email: email of the customer
     * @param gymUser: the user trying to change the account type
     * @return the new instructor
     * @throws GRSException invalid customer email or not an owner
     */
    public Instructor changeAccountType(String email, GymUser gymUser) {
        if (gymUser instanceof Owner == false) {
            throw new GRSException(HttpStatus.UNAUTHORIZED, "Only owners can change account type.");
        }
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null) {
            throw new GRSException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        Person person = customer.getPerson();
        Instructor instructor = new Instructor();
        instructor.setEmail(email);
        instructor.setPassword(customer.getPassword());
        instructor.setPerson(person);
        instructorRepository.save(instructor);
        customerRepository.deleteCustomerByEmail(email);
        return instructor;
    }
}
