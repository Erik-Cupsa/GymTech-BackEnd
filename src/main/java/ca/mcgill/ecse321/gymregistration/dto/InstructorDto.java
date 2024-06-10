package ca.mcgill.ecse321.gymregistration.dto;

import ca.mcgill.ecse321.gymregistration.model.Instructor;
import ca.mcgill.ecse321.gymregistration.model.Person;

public class InstructorDto extends GymUserDto{
    
      // Hibernate needs a default constructor
      public InstructorDto()
      {
        super();
      }
    
      public InstructorDto(String aEmail, String aPassword, Person aPerson)
      {
        super(aEmail, aPassword, aPerson);
      }

      public InstructorDto(Instructor instructor)
      {
        super(instructor.getEmail(), instructor.getPassword(), instructor.getPerson());
        this.setId(instructor.getId());
      }
    
}


