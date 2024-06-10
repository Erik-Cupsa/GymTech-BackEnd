package ca.mcgill.ecse321.gymregistration.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GymUser
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //GymUser Attributes
  private String email;
  private String password;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  //GymUser Associations
  @ManyToOne
  private Person person;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public GymUser() {
  }

  public GymUser(String email, String password, Person person) {
    this.email = email;
    this.password = password;
    this.person = person;
  }

  //------------------------
  // INTERFACE
  //------------------------


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  @Override
  public String toString() {
    return "GymUser{" +
            "email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", id=" + id +
            ", person=" + person +
            '}';
  }
}