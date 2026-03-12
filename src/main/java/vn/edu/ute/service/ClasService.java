package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.ClassStatus;
import vn.edu.ute.dto.ClasView;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Course;

import java.util.List;

public interface ClasService {
    List<Clas> getAll() throws Exception;
    void insert(Clas clas) throws Exception;
    void update(Clas clas) throws Exception;
    void delete(Long id) throws Exception;
    Clas findById(Long id) throws Exception;
    List<ClasView> toClasView(List<Clas> classes);
    List<Clas> getClasViewsByStatus(List<Clas> classes, ClassStatus classStatus);
    List<Clas> getClasViewsByCourse(List<Clas> classes, Course course);
    List<Clas> getClasViewsByBranch(List<Clas> classes, Branch branch);
    List<Clas> findByName(List<Clas> classes, String name);
    List<Clas> getAllActiveClasses() throws Exception;
}
