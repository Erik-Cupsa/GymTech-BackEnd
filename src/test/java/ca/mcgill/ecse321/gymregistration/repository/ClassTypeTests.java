package ca.mcgill.ecse321.gymregistration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.gymregistration.dao.ClassTypeRepository;
import ca.mcgill.ecse321.gymregistration.model.ClassType;

@SpringBootTest
public class ClassTypeTests {
    @Autowired
    private ClassTypeRepository repo;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        repo.deleteAll();
    }

    @Test
    public void testCreateAndReadApprovedClassType() {
        // Create approved class type.
        String name = "testApprovedClassType";
        boolean isApproved = true;
        ClassType approvedClassType = new ClassType(name, isApproved);
        // Save approved class type.
        approvedClassType = repo.save(approvedClassType);
        // Read approved class type from database.
        ClassType approvedClassTypeFromDB = repo.findClassTypeById(approvedClassType.getId());
        // Assert approved class type is not null and has correct attributes.
        assertNotNull(approvedClassTypeFromDB);
        assertEquals(approvedClassType.getId(), approvedClassTypeFromDB.getId());
        assertEquals(name, approvedClassTypeFromDB.getName());
        assertEquals(isApproved, approvedClassTypeFromDB.getIsApproved());
    }

    @Test
    public void testCreateAndReadNotApprovedClassType() {
        // Create not approved class type.
        String name = "testNotApprovedClassType";
        boolean isApproved = false;
        ClassType notApprovedClassType = new ClassType(name, isApproved);
        // Save not approved class type.
        notApprovedClassType = repo.save(notApprovedClassType);
        // Read not approved class type from database.
        ClassType notApprovedClassTypeFromDB = repo.findClassTypeById(notApprovedClassType.getId());
        // Assert not approved class type is not null and has correct attributes.
        assertNotNull(notApprovedClassTypeFromDB);
        assertEquals(notApprovedClassType.getId(), notApprovedClassTypeFromDB.getId());
        assertEquals(name, notApprovedClassTypeFromDB.getName());
        assertEquals(isApproved, notApprovedClassTypeFromDB.getIsApproved());
    }
}
