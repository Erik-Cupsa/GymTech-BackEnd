package ca.mcgill.ecse321.gymregistration.model;


import java.sql.Date;

import jakarta.persistence.*;

/**
 * Registration class for clients
 * => those that want to participate in a session
 * => includes all users (instead of switching accounts)
 */
@Entity
public class CustomerRegistration
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //CustomerRegistration Attributes
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private Date date;

  //CustomerRegistration Associations
  @ManyToOne
  private Session session;
  @ManyToOne
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Hibernate needs a default constructor
  public CustomerRegistration() {
  }

  public CustomerRegistration(Date date, Session session, Customer customer) {
    this.date = date;
    this.session = session;
    this.customer = customer;
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

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  @Override
  public String toString() {
    return "CustomerRegistration{" +
            "id=" + id +
            ", date=" + date +
            ", session=" + session +
            ", customer=" + customer +
            '}';
  }
}