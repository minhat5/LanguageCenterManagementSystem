package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;

import java.util.List;
import java.util.Optional;

public interface BranchRepo {
    // Tìm tất cả chi nhánh
    List<Branch> findAll(EntityManager em);
    Branch save(EntityManager em, Branch branch);
    Optional<Branch> findById(EntityManager em, Long id);
    void deleteById(EntityManager em, Long id);
}
