package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.ClassType;

public class ClassTypeDto {
    //ClassType Attributes
    private int id;
    private String name;
    private boolean isApproved;

    //all constructors
    public ClassTypeDto(){
    }

    /**
     * ClassTypeDto: constructor all attributes
     * @param id
     * @param name
     * @param isApproved
     */
    public ClassTypeDto(int id, String name, boolean isApproved) {
        this.id = id;
        this.name = name;
        this.isApproved = isApproved;
    }



    /**
     * ClassTypeDto: constructor no id
     * @param name
     * @param isApproved
     */
    public ClassTypeDto(String name, boolean isApproved) {
        this.name = name;
        this.isApproved = isApproved;
    }

    /**
     * ClassTypeDto: constructor from ClassType
     * @param classType
     */
    public ClassTypeDto(ClassType classType) {
        this.id = classType.getId();
        this.name = classType.getName();
        this.isApproved = classType.getIsApproved();
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
