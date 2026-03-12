package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepo;

import java.util.List;
import java.util.Optional;

public class BranchRepoImpl implements BranchRepo {

    @Override
    public List<Branch> findAll(EntityManager em) {
        return em.createQuery("select b from Branch b", Branch.class).getResultList();
    }
    @Override
    public Branch save(EntityManager em, Branch branch) {
        if(branch.getBranchId() == null) {
            em.persist(branch);
            return branch;
        }
        else {
            return em.merge(branch);
        }
    }

    @Override
    public Optional<Branch> findById(EntityManager em, Long id){
        Branch branch = em.find(Branch.class, id);
        return Optional.ofNullable(branch);
    }
    @Override
    public void deleteById(EntityManager em, Long id) {
        Branch branch = em.find(Branch.class, id);
        if (branch != null) {
            em.remove(branch);
        }
    }
}
