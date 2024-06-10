package ca.mcgill.ecse321.gymregistration.dao;

import ca.mcgill.ecse321.gymregistration.model.Session;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Integer> {
    Session findSessionById(int sessionId);
    List<Session> findSessionsByClassType_Name(String name);
    Session findSessionByStartTimeAndDate(Time startTime, Date date);
    List<Session> findAll();

    void deleteSessionById(int id);
}
