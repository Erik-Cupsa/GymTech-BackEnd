package ca.mcgill.ecse321.gymregistration.model;


import java.sql.Date;
import jakarta.persistence.*;

@Entity
public class InstructorRegistration
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //InstructorRegistration Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private Date date;

  //InstructorRegistration Associations
  @ManyToOne
  private Instructor instructor;
  @ManyToOne
  private Session session;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public InstructorRegistration() {
  }

  public InstructorRegistration(Date date, Instructor instructor, Session session) {
    this.date = date;
    this.instructor = instructor;
    this.session = session;
  }

    public InstructorRegistration(InstructorRegistration instructorRegistration) {
      this.date = instructorRegistration.getDate();
      this.instructor = instructorRegistration.getInstructor();
      this.session = instructorRegistration.getSession();
    }

    //------------------------
  // INTERFACE
  //------------------------


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

  @Override
  public String toString() {
    return "InstructorRegistration{" +
            "id=" + id +
            ", date=" + date +
            ", instructor=" + instructor +
            ", session=" + session +
            '}';
  }
}