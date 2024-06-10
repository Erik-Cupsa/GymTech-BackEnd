package ca.mcgill.ecse321.gymregistration.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Person
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Person Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private String name;
  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public Person() {
  }

  public Person(String name) {
    this.name = name;
  }

  //------------------------
  // INTERFACE
  //------------------------


  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "Person{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}