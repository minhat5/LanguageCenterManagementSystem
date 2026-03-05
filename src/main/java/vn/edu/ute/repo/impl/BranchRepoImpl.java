package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepo;

import java.util.List;

public class BranchRepoImpl implements BranchRepo {

    @Override
    public List<Branch> findAll(EntityManager em) {
        return em.createQuery("select b from Branch b", Branch.class).getResultList();
    }

    @Override
    public Branch findById(EntityManager em, int id) {
        return em.find(Branch.class, id);
    }
}
