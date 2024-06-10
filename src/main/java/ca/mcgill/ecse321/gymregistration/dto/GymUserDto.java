package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.GymUser;
import ca.mcgill.ecse321.gymregistration.model.Person;

public class GymUserDto {
    //gym user attributes
    private int id;
    private String email;
    private String password;
    private Person person;

    public GymUserDto(){
    }

    /**
     * GymUserDto: gym user dto constructor all attributes
     * @param id
     * @param email
     * @param password
     * @param person
     */

    public GymUserDto(int id, String email, String password, Person person) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.person = person;
    }

    /**
     * GymUserDto: gym user dto constructor no id
     * @param email
     * @param password
     * @param person
     */
    public GymUserDto(String email, String password, Person person) {
        this.email = email;
        this.password = password;
        this.person = person;
    }

    /**
     * GymUserDto: constructor from GymUser
     * @param gymUser
     */

    public GymUserDto(GymUser gymUser){
        this.id = gymUser.getId();
        this.email = gymUser.getEmail();
        this.password = gymUser.getPassword();
        this.person = gymUser.getPerson();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
