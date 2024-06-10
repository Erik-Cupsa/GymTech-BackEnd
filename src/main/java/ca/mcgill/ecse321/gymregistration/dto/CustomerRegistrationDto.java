package ca.mcgill.ecse321.gymregistration.dto;

import java.sql.Date;

import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.CustomerRegistration;
import ca.mcgill.ecse321.gymregistration.model.Session;

public class CustomerRegistrationDto {
    
    //CustomerRegistration Attributes
    private int id;
    private Date date;
    private Session session;
    private Customer customer;


    // Empty constructor
    public CustomerRegistrationDto(){
    }

    /**
     * CustomerRegistrationDto: constructor from CustomerRegistration
     * @param customerRegistration
     */
    public CustomerRegistrationDto(CustomerRegistration customerRegistration){
        this.id = customerRegistration.getId();
        this.date = customerRegistration.getDate();
        this.session = customerRegistration.getSession();
        this.customer = customerRegistration.getCustomer();
    }

    /**
     * CustomerRegistrationDto: constructor from all attributes
     * @param id
     * @param date
     * @param session
     * @param customer
     */
    public CustomerRegistrationDto(int id, Date date, Session session, Customer customer){
        this.id = id;
        this.date = date;
        this.session = session;
        this.customer = customer;
    }

    
    // Getters and Setters for all attributes

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }




}
