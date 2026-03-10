package vn.edu.ute.service;

import vn.edu.ute.enumeration.EnrollmentStatus;
import vn.edu.ute.enumeration.Result;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.PlacementTest;

import java.math.BigDecimal;
import java.util.List;

public interface EnrollmentService {
    void submitPlacementTest(Long studentId, BigDecimal score, String note) throws Exception;
    List<Clas> getSuggestedClasses(Long studentId) throws Exception;
    void enrollStudent(Long studentId, Long classId) throws Exception;
    List<PlacementTest> getAllPlacementTests() throws Exception;
    void updatePlacementTest(Long testId, BigDecimal newScore, String newNote) throws Exception;
    void deletePlacementTest(Long testId) throws Exception;
    List<Enrollment> getAllEnrollments() throws Exception;
    void updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus newStatus) throws Exception;
    List<Clas> getAllClasses() throws Exception;
    void updateEnrollment(Long enrollmentId, Long newClassId, EnrollmentStatus newStatus, Result newResult) throws Exception;
}