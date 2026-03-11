package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;

import java.util.List;
import java.util.Optional;

public class TeacherRepositoryImpl implements TeacherRepository {

    @Override
    public Teacher save(EntityManager em, Teacher teacher) {
        if (teacher.getTeacherId() == null) {
            em.persist(teacher);
            return teacher;
        } else {
            return em.merge(teacher);
        }
    }

    @Override
    public Optional<Teacher> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Teacher.class, id));
    }

    @Override
    public List<Teacher> findAll(EntityManager em) {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        Teacher teacher = em.find(Teacher.class, id);
        if (teacher != null) {
            em.remove(teacher);
        }
    }

    @Override
    public boolean existsByEmail(EntityManager em, String email) {
        Long count = em.createQuery("SELECT COUNT(t) FROM Teacher t WHERE t.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByPhone(EntityManager em, String phone) {
        Long count = em.createQuery("SELECT COUNT(t) FROM Teacher t WHERE t.phone = :phone", Long.class)
                .setParameter("phone", phone)
                .getSingleResult();
        return count > 0;
    }
}
