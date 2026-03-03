package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;
import vn.edu.ute.repo.ClasRepo;

import java.util.List;

public class ClasRepoImpl implements ClasRepo {
    private final EntityManager em;

    public ClasRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Clas findById(Long clasId) {
        // Hàm find() của JPA tự động tìm theo Khóa chính (Primary Key)
        return em.find(Clas.class, clasId);
    }

    @Override
    public List<Clas> findAll() {
        return em.createQuery("SELECT c FROM Clas c", Clas.class).getResultList();
    }
}
