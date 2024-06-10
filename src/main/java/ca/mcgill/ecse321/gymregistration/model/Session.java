package ca.mcgill.ecse321.gymregistration.model;


import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.*;

@Entity
public class Session
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Session Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private Date date;
  private Time startTime;
  private Time endTime;
  private String description;
  private String name;
  private String location;
  private int capacity;

  //Session Associations
  
  @ManyToOne
  @JoinColumn(name = "class_type_id")
  private ClassType classType;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public Session() {
  }

  public Session(Date date, Time startTime, Time endTime, String description, String name, String location, ClassType classType, int capacity) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.description = description;
    this.name = name;
    this.location = location;
    this.classType = classType;
    this.capacity = capacity;
  }
  public Session(Date date, Time startTime, Time endTime, String description, String name, String location, ClassType classType) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.description = description;
    this.name = name;
    this.location = location;
    this.classType = classType;
  }

    public Session(Session session) {
      this.date = session.getDate();
      this.startTime = session.getStartTime();
      this.endTime = session.getEndTime();
      this.description = session.getName();
      this.name = session.getName();
      this.location = session.getLocation();
      this.classType = session.getClassType();
    }
    //------------------------
  // INTERFACE
  //------------------------

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
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

  public Time getStartTime() {
    return startTime;
  }

  public void setStartTime(Time startTime) {
    this.startTime = startTime;
  }

  public Time getEndTime() {
    return endTime;
  }

  public void setEndTime(Time endTime) {
    this.endTime = endTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public ClassType getClassType() {
    return classType;
  }

  public void setClassType(ClassType classType) {
    this.classType = classType;
  }

  @Override
  public String toString() {
    return "Session{" +
            "id=" + id +
            ", date=" + date +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", description='" + description + '\'' +
            ", name='" + name + '\'' +
            ", location='" + location + '\'' +
            ", classType=" + classType +
            '}';
  }
}