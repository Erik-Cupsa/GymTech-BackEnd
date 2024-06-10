package ca.mcgill.ecse321.gymregistration.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends GymUser {

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Customer Attributes
  private String creditCardNumber;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public Customer() {
    super();
    creditCardNumber = "";
  }

  public Customer(String aEmail, String aPassword, Person aPerson) {
    super(aEmail, aPassword, aPerson);
    creditCardNumber = "";
  }
  
  public Customer(String aEmail, String aPassword, Person aPerson, String aCreditCardNumber) {
    super(aEmail, aPassword, aPerson);
    creditCardNumber = aCreditCardNumber;
  }

  //------------------------
  // INTERFACE
  //------------------------


  public String getCreditCardNumber() {
    return creditCardNumber;
  }

  public void setCreditCardNumber(String creditCardNumber) {
    this.creditCardNumber = creditCardNumber;

  }

  @Override
  public String toString() {
    return "Customer{" +
            "creditCardNumber=" + creditCardNumber +
            '}';
  }
}