package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepository;
import vn.edu.ute.service.BranchService;

import java.util.List;

public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepo;
    private final TransactionManager txManager;

    public BranchServiceImpl(BranchRepository branchRepo, TransactionManager txManager) {
        this.branchRepo = branchRepo;
        this.txManager = txManager;
    }

    @Override
    public Branch saveBranch(Branch branch) throws Exception {
        return txManager.runInTransaction(em -> branchRepo.save(em, branch));
    }

    @Override
    public Branch getBranchById(Long id) throws Exception {
        return txManager.runInTransaction(em -> branchRepo.findById(em, id).orElse(null));
    }

    @Override
    public List<Branch> getAllBranches() throws Exception {
        return txManager.runInTransaction(em -> branchRepo.findAll(em));
    }

    @Override
    public void deleteBranch(Long id) throws Exception {
        txManager.runInTransaction(em -> {
            branchRepo.deleteById(em, id);
            return null;
        });
    }

    @Override
    public List<Branch> filterBranches(String keyword, vn.edu.ute.common.enumeration.Status status, String addressKeyword) throws Exception {
        return getAllBranches().stream()
            .filter(b -> {
                boolean matchKw = (keyword == null || keyword.trim().isEmpty()) ||
                                  (b.getBranchName() != null && b.getBranchName().toLowerCase().contains(keyword.toLowerCase())) ||
                                  (b.getPhone() != null && b.getPhone().contains(keyword));
                boolean matchSt = (status == null) || (b.getStatus() == status);
                boolean matchAddr = (addressKeyword == null || addressKeyword.trim().isEmpty() || addressKeyword.equals("Tất cả")) ||
                                    (b.getAddress() != null && b.getAddress().toLowerCase().contains(addressKeyword.toLowerCase()));
                return matchKw && matchSt && matchAddr;
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
