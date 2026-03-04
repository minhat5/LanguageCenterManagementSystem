package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;

import java.util.List;

public interface ClasRepo {
    List<Clas> findAll(EntityManager em);
    void insert(EntityManager em, Clas clas);
    void update(EntityManager em, Clas clas);
    void delete(EntityManager em, Long id);
    Clas findById(EntityManager em, Long id);
}
