package ca.mcgill.ecse321.gymregistration.model;

import jakarta.persistence.Entity;
@Entity
public class Owner extends GymUser
{
  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public Owner()
  {
    super();
  }

  public Owner(String aEmail, String aPassword, Person aPerson)
  {
    super(aEmail, aPassword, aPerson);
  }
}