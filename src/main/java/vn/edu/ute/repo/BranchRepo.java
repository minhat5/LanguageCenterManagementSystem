package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;

import java.util.List;

public interface BranchRepo {
    // Tìm tất cả chi nhánh
    List<Branch> findAll(EntityManager em);
}
