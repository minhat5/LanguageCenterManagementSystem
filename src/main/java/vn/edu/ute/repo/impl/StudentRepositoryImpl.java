package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepository;
import java.util.List;
import java.util.Optional;

public class StudentRepositoryImpl implements StudentRepository {

    @Override
    public Student save(EntityManager em, Student student) {
        if (student.getStudentId() == null) {
            em.persist(student);
            return student;
        } else {
            return em.merge(student);
        }
    }

    @Override
    public Optional<Student> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    @Override
    public boolean existsByEmail(EntityManager em, String email) {
        Long count = em.createQuery("SELECT COUNT(s) FROM Student s WHERE s.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByPhone(EntityManager em, String phone) {
        Long count = em.createQuery("SELECT COUNT(s) FROM Student s WHERE s.phone = :phone", Long.class)
                .setParameter("phone", phone)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Student> findAll(EntityManager em) {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        Student student = em.find(Student.class, id);
        if (student != null) {
            em.remove(student);
        }
    }
}
