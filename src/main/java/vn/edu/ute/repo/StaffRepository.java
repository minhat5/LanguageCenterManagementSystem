package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Staff;

import java.util.Optional;

public interface StaffRepository {
    Staff save(EntityManager em, Staff staff);
    Optional<Staff> findById(EntityManager em, Long staffId);
    java.util.List<Staff> findAll(EntityManager em);
    void deleteById(EntityManager em, Long staffId);
}
