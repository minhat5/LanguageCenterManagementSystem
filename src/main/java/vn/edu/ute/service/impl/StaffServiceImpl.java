package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.StaffRepository;
import vn.edu.ute.repo.UserAccountRepository;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.common.util.PasswordUtil;

public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepo;
    private final UserAccountRepository userAccountRepo;
    private final TransactionManager txManager;

    public StaffServiceImpl(StaffRepository staffRepo, UserAccountRepository userAccountRepo, TransactionManager txManager) {
        this.staffRepo = staffRepo;
        this.userAccountRepo = userAccountRepo;
        this.txManager = txManager;
    }

    @Override
    public Staff createStaffAccount(Staff staff, String username, String initialPassword) throws Exception {
        return txManager.runInTransaction(em -> {
            if (userAccountRepo.existsByUsername(em, username)) {
                throw new Exception("Username already exists: " + username);
            }

            // Save staff first to generate ID
            Staff savedStaff = staffRepo.save(em, staff);

            // Create and link user account
            UserAccount account = new UserAccount();
            account.setUsername(username);
            account.setPasswordHash(PasswordUtil.hashPassword(initialPassword));
            account.setRole(staff.getRole());
            account.setIsActive(true);
            account.setStaff(savedStaff);

            userAccountRepo.save(em, account);
            
            savedStaff.setUserAccount(account);
            return savedStaff;
        });
    }

    @Override
    public java.util.List<Staff> getAllStaffs() throws Exception {
        return txManager.runInTransaction(em -> {
            return staffRepo.findAll(em);
        });
    }

    @Override
    public Staff updateStaff(Staff staff) throws Exception {
        return txManager.runInTransaction(em -> {
            return staffRepo.save(em, staff);
        });
    }

    @Override
    public void deleteStaff(Long id) throws Exception {
        txManager.runInTransaction(em -> {
            staffRepo.deleteById(em, id);
            return null; // runInTransaction expects a return value
        });
    }

    @Override
    public java.util.List<Staff> filterStaffs(String keyword, vn.edu.ute.common.enumeration.Role role, vn.edu.ute.common.enumeration.Status status) throws Exception {
        return getAllStaffs().stream()
                .filter(s -> {
                    boolean matchKeyword = true;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String kw = keyword.toLowerCase().trim();
                        matchKeyword = (s.getFullName() != null && s.getFullName().toLowerCase().contains(kw)) ||
                                       (s.getPhone() != null && s.getPhone().contains(kw));
                    }
                    boolean matchRole = (role == null) || (s.getRole() == role);
                    boolean matchStatus = (status == null) || (s.getStatus() == status);
                    return matchKeyword && matchRole && matchStatus;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
