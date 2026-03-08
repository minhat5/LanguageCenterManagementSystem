package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepo;
import vn.edu.ute.service.BranchService;

import java.util.List;

public class BranchServiceImpl implements BranchService {
    private final BranchRepo branchRepo;
    private final TransactionManager tx;

    public BranchServiceImpl(BranchRepo branchRepo, TransactionManager tx) {
        this.branchRepo = branchRepo;
        this.tx = tx;
    }

    // Lấy tất cả chi nhánh
    @Override
    public List<Branch> getAll() throws Exception {
        return tx.runInTransaction(branchRepo::findAll);
    }
}
