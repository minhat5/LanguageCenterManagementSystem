package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepo; // Ưu tiên tên class này nếu bạn dùng JPA/EntityManager
import vn.edu.ute.service.BranchService;

import java.util.List;
import java.util.stream.Collectors;

public class BranchServiceImpl implements BranchService {

    private final BranchRepo branchRepo;
    private final TransactionManager txManager;

    public BranchServiceImpl(BranchRepo branchRepo, TransactionManager txManager) {
        this.branchRepo = branchRepo;
        this.txManager = txManager;
    }

    @Override
    public List<Branch> getAll() throws Exception {
        return getAllBranches();
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
    public List<Branch> filterBranches(String keyword, Status status, String addressKeyword) throws Exception {
        // Tạo stream để xử lý tập hợp danh sách các chi nhánh
        return getAllBranches().stream()
                // Sử dụng filter để kiểm tra các phần tử thoả mãn yêu cầu từ khóa
                .filter(b -> {
                    boolean matchKw = (keyword == null || keyword.trim().isEmpty()) ||
                            (b.getBranchName() != null
                                    && b.getBranchName().toLowerCase().contains(keyword.toLowerCase()))
                            ||
                            (b.getPhone() != null && b.getPhone().contains(keyword));
                    boolean matchSt = (status == null) || (b.getStatus() == status);
                    boolean matchAddr = (addressKeyword == null || addressKeyword.trim().isEmpty()
                            || addressKeyword.equals("Tất cả")) ||
                            (b.getAddress() != null
                                    && b.getAddress().toLowerCase().contains(addressKeyword.toLowerCase()));
                    return matchKw && matchSt && matchAddr;
                })
                // Triển khai thao tác gom kết quả (collect) vào cấu trúc danh sách
                .collect(Collectors.toList());
    }
}