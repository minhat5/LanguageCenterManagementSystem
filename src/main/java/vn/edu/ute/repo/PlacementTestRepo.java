package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;

import java.util.List;

public interface PlacementTestRepo {
    void save (EntityManager em, PlacementTest test);
    List<PlacementTest> findByStudentId(EntityManager em, Long studentId);
    List<PlacementTest> findAll(EntityManager em);
    PlacementTest findById(EntityManager em, Long testId);
    void delete(EntityManager em, Long testId);
}
