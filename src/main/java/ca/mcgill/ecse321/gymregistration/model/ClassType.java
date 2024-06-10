package ca.mcgill.ecse321.gymregistration.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class ClassType
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //ClassType Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private String name;
  private boolean isApproved;

  @OneToMany(mappedBy = "classType", cascade = CascadeType.ALL)
  private List<Session> sessions;

//------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public ClassType() {
  }

  public ClassType(String name, boolean isApproved) {
    this.name = name;
    this.isApproved = isApproved;
  }

  //------------------------
  // INTERFACE
  //------------------------

  //all getters and setters
  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setApproved(boolean approved) {
    this.isApproved = approved;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean getIsApproved() {
    return isApproved;
  }

  @Override
  public String toString() {
    return "ClassType{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", isApproved=" + isApproved +
            '}';
  }
}