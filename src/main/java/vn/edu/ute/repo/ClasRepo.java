package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Clas;

import java.util.List;

public interface ClasRepo {
    Clas findById (EntityManager em, Long clasId);
    List<Clas> findAll(EntityManager em);
}
