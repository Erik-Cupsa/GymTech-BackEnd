
package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.Owner;
import ca.mcgill.ecse321.gymregistration.model.Person;

public class OwnerDto extends GymUserDto{
    //owner attributes are in the super class
    // Hibertnate needs a default constructor
    public OwnerDto() {
        super(); // Call constructor of superclass
    }
    public OwnerDto(String email, String password, Person person) {
        super(email, password, person); // Call constructor of superclass
    }

    public OwnerDto(Owner owner){
        super(owner.getEmail(), owner.getPassword(), owner.getPerson());
        this.setId(owner.getId());
      }

}