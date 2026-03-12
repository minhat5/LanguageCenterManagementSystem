package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.repo.ScheduleRepo;

import java.util.List;

public class ScheduleRepoImpl implements ScheduleRepo {

    @Override
    public List<Schedule> findAll(EntityManager em) {
        String jpql = "SELECT s FROM Schedule s " +
                "LEFT JOIN FETCH s.clas c " +
                "LEFT JOIN FETCH c.course " +
                "LEFT JOIN FETCH s.room r " +
                "LEFT JOIN FETCH r.branch";
        return em.createQuery(jpql, Schedule.class).getResultList();
    }

    @Override
    public void insert(EntityManager em, Schedule schedule) {
        em.persist(schedule);
    }

    @Override
    public void update(EntityManager em, Schedule schedule) {
        em.merge(schedule);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Schedule schedule = em.find(Schedule.class, id);
        if(schedule == null) {
            throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học " + id);
        }
        em.remove(schedule);
    }

    @Override
    public Schedule findById(EntityManager em, Long id) {
        String jpql = "SELECT s FROM Schedule s " +
                "LEFT JOIN FETCH s.clas c " +
                "LEFT JOIN FETCH c.course " +
                "LEFT JOIN FETCH s.room r " +
                "LEFT JOIN FETCH r.branch " +
                "WHERE s.scheduleId = :id";
        return em.createQuery(jpql, Schedule.class).setParameter("id", id).getSingleResult();
    }
}
