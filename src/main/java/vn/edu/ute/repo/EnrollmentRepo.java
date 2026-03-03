package vn.edu.ute.repo;

import vn.edu.ute.model.Enrollment;

import java.util.List;

public interface EnrollmentRepo {
    void save(Enrollment enrollment);
    List<Enrollment> findAll();
}
