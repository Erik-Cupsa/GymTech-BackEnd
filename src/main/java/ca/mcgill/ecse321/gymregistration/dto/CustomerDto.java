package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.Customer;
import ca.mcgill.ecse321.gymregistration.model.Person;

public class CustomerDto extends GymUserDto{
    //customer attributes
    private String creditCardNumber;
    public CustomerDto() {
        super(); // Call constructor of superclass
    }
    public CustomerDto(String email, String password, Person person, String creditCardNumber) {
        super(email, password, person); // Call constructor of superclass
        this.creditCardNumber = creditCardNumber;
    }
    public CustomerDto(String email, String password, Person person) {
        super(email, password, person); // Call constructor of superclass
        this.creditCardNumber = "";
    }
    public CustomerDto(Customer customer){
        super(customer.getId(), customer.getEmail(), customer.getPassword(), customer.getPerson());
        this.creditCardNumber = customer.getCreditCardNumber();
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}
