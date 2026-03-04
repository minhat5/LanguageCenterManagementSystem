package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;
import vn.edu.ute.repo.ClasRepo;

import java.util.List;

public class ClasRepoImpl implements ClasRepo {
    @Override
    public List<Clas> findAll(EntityManager em) {
        return em.createQuery("select c from Clas c").getResultList();
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
        Clas clas = em.find(Clas.class, id);
        if(clas == null) {
            throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học " + id);
        }
        em.remove(clas);
    }

    @Override
    public Clas findById(EntityManager em, Long id) {
        return em.find(Clas.class, id);
    }
}
