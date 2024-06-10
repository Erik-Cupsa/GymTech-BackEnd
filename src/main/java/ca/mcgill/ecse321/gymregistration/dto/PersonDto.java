package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.Person;

public class PersonDto {
    //person attributes
    private int id;
    private String name;

    public PersonDto() {
    }

    public PersonDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public PersonDto(String name) {
        this.name = name;
    }
    public PersonDto(Person person) {
        this.id = person.getId();
        this.name = person.getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
