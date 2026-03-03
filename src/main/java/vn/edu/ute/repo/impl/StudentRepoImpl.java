package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepo;

public class StudentRepoImpl implements StudentRepo {
    private final EntityManager em;

    public StudentRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Student findById(Long studentId) {
        // Trả về thực thể Student hoặc null nếu không tìm thấy
        return em.find(Student.class, studentId);
    }
}
