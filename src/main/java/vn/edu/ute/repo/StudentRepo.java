package vn.edu.ute.repo;

import vn.edu.ute.model.Student;

public interface StudentRepo {
    Student findById(Long studentId);
}
