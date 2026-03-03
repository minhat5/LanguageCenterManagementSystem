package vn.edu.ute.repo;

import vn.edu.ute.model.PlacementTest;

import java.util.List;

public interface PlacementTestRepo {
    void save (PlacementTest test);
    List<PlacementTest> findByStudentId(Long studentId);
}
