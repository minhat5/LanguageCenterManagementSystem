package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;

import java.util.List;

public interface ScheduleRepo {
    List<Schedule> findAll(EntityManager em);
    void insert(EntityManager em, Schedule schedule);
    void update(EntityManager em, Schedule schedule);
    void delete(EntityManager em, Long id);
    Schedule findById(EntityManager em, Long id);
}
