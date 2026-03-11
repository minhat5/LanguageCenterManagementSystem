package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;
import java.util.List;
import java.util.Optional;

public interface BranchRepository {
    Branch save(EntityManager em, Branch branch);
    Optional<Branch> findById(EntityManager em, Long id);
    List<Branch> findAll(EntityManager em);
    void deleteById(EntityManager em, Long id);
}
