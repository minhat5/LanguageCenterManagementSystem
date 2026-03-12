package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;
import vn.edu.ute.repo.ClasRepo;

import java.util.List;

public class ClasRepoImpl implements ClasRepo {
    @Override
    public List<Clas> findAll(EntityManager em) {
        // Sử dụng JPQL để truy vấn tất cả các lớp học cùng với thông tin liên quan tránh bị lỗi LazyInitializationException khi truy cập các thuộc tính liên quan sau này
        String jpql = "SELECT c FROM Clas c " +
                "LEFT JOIN FETCH c.course " +
                "LEFT JOIN FETCH c.teacher " +
                "LEFT JOIN FETCH c.branch " +
                "LEFT JOIN FETCH c.room";
        return em.createQuery(jpql, Clas.class).getResultList();
    }

    @Override
    public void insert(EntityManager em, Clas clas) {
        em.persist(clas);
    }

    @Override
    public void update(EntityManager em, Clas clas) {
        em.merge(clas);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        //Kiểm tra xem lớp học có tồn tại không trước khi xoá
        Clas clas = em.find(Clas.class, id);
        if(clas == null) {
            throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học " + id);
        }
        em.remove(clas);
    }

    @Override
    public Clas findById(EntityManager em, Long id) {
        String jpql = "SELECT c FROM Clas c " +
                "LEFT JOIN FETCH c.course " +
                "LEFT JOIN FETCH c.teacher " +
                "LEFT JOIN FETCH c.branch " +
                "LEFT JOIN FETCH c.room " +
                "WHERE c.classId = :id";
        return em.createQuery(jpql, Clas.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
