package ca.mcgill.ecse321.gymregistration.model;

import jakarta.persistence.Entity;

@Entity
public class Instructor extends GymUser
{

  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public Instructor()
  {
    super();
  }

  public Instructor(String aEmail, String aPassword, Person aPerson)
  {
    super(aEmail, aPassword, aPerson);
  }

}