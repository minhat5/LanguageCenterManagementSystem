package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepository;

import java.util.List;
import java.util.Optional;
public class BranchRepositoryImpl implements BranchRepository {
	
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
	public List<Branch> findAll(EntityManager em){
		return em.createQuery("Select b from Branch b", Branch.class).getResultList();
	}
	
	@Override
	public void deleteById(EntityManager em, Long id) {
		Branch branch = em.find(Branch.class, id);
		if (branch != null) {
			em.remove(branch);
		}
	}
}
