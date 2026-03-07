package vn.edu.ute.service;

import vn.edu.ute.model.Branch;

import java.util.List;

public interface BranchService {
    List<Branch> getAll() throws Exception;
}
