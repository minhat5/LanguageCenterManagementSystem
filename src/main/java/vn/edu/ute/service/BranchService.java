package vn.edu.ute.service;

import vn.edu.ute.model.Branch;
import vn.edu.ute.common.enumeration.Status;
import java.util.List;

public interface BranchService {
    List<Branch> getAll() throws Exception;
    Branch saveBranch(Branch branch) throws Exception;
    Branch getBranchById(Long id) throws Exception;
    List<Branch> getAllBranches() throws Exception;
    void deleteBranch(Long id) throws Exception;
    List<Branch> filterBranches(String keyword, Status status, String addressKeyword) throws Exception;
}