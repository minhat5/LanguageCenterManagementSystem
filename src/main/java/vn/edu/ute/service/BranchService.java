package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepo;

import java.util.List;

public class BranchService {
    private final BranchRepo branchRepo;
    private final TransactionManager tx;

    public BranchService(BranchRepo branchRepo, TransactionManager tx) {
        this.branchRepo = branchRepo;
        this.tx = tx;
    }

    public List<Branch> getAll() throws Exception {
        return tx.runInTransaction(branchRepo::findAll);
    }
}
