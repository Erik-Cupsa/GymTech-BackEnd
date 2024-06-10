package ca.mcgill.ecse321.gymregistration.dto;

import java.sql.Date;

import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.InstructorRegistration;
import ca.mcgill.ecse321.gymregistration.model.Session;

public class InstructorRegistrationDto{


//------------------------
  // MEMBER VARIABLES
  //------------------------

  //InstructorRegistration Attributes

  private int id;
  private Date date;

  //InstructorRegistration Associations

  private Instructor instructor;

  private Session session;


  public InstructorRegistrationDto() {
  }

  public InstructorRegistrationDto(Date date, Instructor instructor, Session session) {
    this.date = date;
    this.instructor = instructor;
    this.session = session;
  }

 public InstructorRegistrationDto(InstructorRegistration instructorRegistration)
 {
  this.date = instructorRegistration.getDate();
  this.instructor = instructorRegistration.getInstructor();
  this.session = instructorRegistration.getSession();
  this.id = instructorRegistration.getId();
 }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Instructor getInstructor() {
    return instructor;
  }

  public void setInstructor(Instructor instructor) {
    this.instructor = instructor;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

}
