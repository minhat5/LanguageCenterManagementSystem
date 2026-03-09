package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Attendance;

import java.util.List;

public interface AttendanceRepo {
    List<Attendance> findAll(EntityManager em);
    void insert(EntityManager em, Attendance attendance);
    void update(EntityManager em, Attendance attendance);
    void delete(EntityManager em, Long id);
    Attendance findById(EntityManager em, Long id);
}
