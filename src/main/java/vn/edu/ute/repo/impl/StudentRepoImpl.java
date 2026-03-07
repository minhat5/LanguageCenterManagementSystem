package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepo;

public class StudentRepoImpl implements StudentRepo {

    @Override
    public Student findById(EntityManager em, Long studentId) {
        // Trả về thực thể Student hoặc null nếu không tìm thấy
        return em.find(Student.class, studentId);
    }
}
