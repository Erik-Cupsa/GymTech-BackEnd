package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.ClassType;
import ca.mcgill.ecse321.gymregistration.model.Session;

import java.sql.Date;

public class SessionDto {

  private int id;
  private Date date;
  private String startTime;
  private String endTime;
  private String description;
  private String name;
  
  private String location;
  private ClassType classType;
  private int capacity;

  public SessionDto() {
  }
  public SessionDto(Session sessionDto){
    this.id = sessionDto.getId();
    this.date = sessionDto.getDate();
    this.startTime = sessionDto.getStartTime().toString();
    this.endTime = sessionDto.getEndTime().toString();
    this.description = sessionDto.getDescription();
    this.name = sessionDto.getName();
    this.location = sessionDto.getLocation();
    this.classType = sessionDto.getClassType();
    this.capacity = sessionDto.getCapacity();
  }

  public SessionDto(Date date, String startTime, String endTime, String description, String name, String location, ClassType classType, int capacity) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.description = description;
    this.name = name;
    this.location = location;
    this.classType = classType;
    this.capacity = capacity;
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

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
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
  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }
}

