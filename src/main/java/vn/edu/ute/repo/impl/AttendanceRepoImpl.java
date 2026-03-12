package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.repo.AttendanceRepo;

import java.util.List;

public class AttendanceRepoImpl implements AttendanceRepo {
    @Override
    public List<Attendance> findAll(EntityManager em) {
        String jpql = "SELECT a FROM Attendance a " +
                "JOIN FETCH a.student s " +
                "JOIN FETCH a.clas c ";
        return em.createQuery(jpql, Attendance.class).getResultList();
    }

    @Override
    public void insert(EntityManager em, Attendance attendance) {
        em.persist(attendance);
    }

    @Override
    public void update(EntityManager em, Attendance attendance) {
        em.merge(attendance);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        em.remove(findById(em, id));
    }

    @Override
    public Attendance findById(EntityManager em, Long id) {
        return em.find(Attendance.class, id);
    }

    @Override
    public List<Attendance> findByClassId(EntityManager em, Long classId) {
        String jpql = "SELECT a FROM Attendance a " +
                "JOIN FETCH a.student s " +
                "JOIN FETCH a.clas c " +
                "WHERE c.classId = :classId";
        return em.createQuery(jpql, Attendance.class)
                .setParameter("classId", classId)
                .getResultList();
    }
}
