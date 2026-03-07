package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;
import vn.edu.ute.repo.ClasRepo;

import java.util.List;

public class ClasRepoImpl implements ClasRepo {

    @Override
    public Clas findById(EntityManager em, Long clasId) {
        // Hàm find() của JPA tự động tìm theo Khóa chính (Primary Key)
        return em.find(Clas.class, clasId);
    }

    @Override
    public List<Clas> findAll(EntityManager em) {
        return em.createQuery("SELECT c FROM Clas c", Clas.class).getResultList();
    }
}
