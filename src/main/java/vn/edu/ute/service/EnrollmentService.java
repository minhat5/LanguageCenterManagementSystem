package vn.edu.ute.service;

import vn.edu.ute.model.Clas;
import java.math.BigDecimal;
import java.util.List;

public interface EnrollmentService {
    void submitPlacementTest(Long studentId, BigDecimal score, String note) throws Exception;
    List<Clas> getSuggestedClasses(Long studentId) throws Exception;
    void enrollStudent(Long studentId, Long classId) throws Exception;
}