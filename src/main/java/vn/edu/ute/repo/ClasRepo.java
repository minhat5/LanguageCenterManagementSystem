package vn.edu.ute.repo;

import vn.edu.ute.model.Clas;

import java.util.List;

public interface ClasRepo {
    Clas findById (Long clasId);
    List<Clas> findAll();
}
