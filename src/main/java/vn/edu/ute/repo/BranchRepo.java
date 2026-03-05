package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;

import java.util.List;

public interface BranchRepo {
    List<Branch> findAll(EntityManager em);
    Branch findById(EntityManager em, int id);
}
