package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.StaffRepo;
import vn.edu.ute.repo.UserAccountRepo;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.common.util.PasswordUtil;

public class StaffServiceImpl implements StaffService {

    private final StaffRepo staffRepo;
    private final UserAccountRepo userAccountRepo;
    private final TransactionManager txManager;

    public StaffServiceImpl(StaffRepo staffRepo, UserAccountRepo userAccountRepo, TransactionManager txManager) {
        this.staffRepo = staffRepo;
        this.userAccountRepo = userAccountRepo;
        this.txManager = txManager;
    }

    @Override
    public Staff createStaffAccount(Staff staff, String username, String initialPassword,
            vn.edu.ute.common.enumeration.Role userRole) throws Exception {
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
            account.setRole(userRole);
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
            Staff updated = staffRepo.save(em, staff);
            em.createQuery("UPDATE UserAccount u SET u.isActive = :isActive WHERE u.staff = :staff")
              .setParameter("isActive", staff.getStatus() == vn.edu.ute.common.enumeration.Status.Active)
              .setParameter("staff", updated)
              .executeUpdate();
            return updated;
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
    public java.util.List<Staff> filterStaffs(String keyword, vn.edu.ute.common.enumeration.StaffRole staffRole,
            vn.edu.ute.common.enumeration.Status status) throws Exception {
        // Tạo luồng (stream) dữ liệu từ danh sách nhân viên
        return getAllStaffs().stream()
                // Bước lọc (filter): loại bỏ các phần tử không trùng khớp điều kiện
                .filter(s -> {
                    boolean matchKeyword = true;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String kw = keyword.toLowerCase().trim();
                        matchKeyword = (s.getFullName() != null && s.getFullName().toLowerCase().contains(kw)) ||
                                (s.getPhone() != null && s.getPhone().contains(kw));
                    }
                    boolean matchRole = (staffRole == null) || (s.getStaffRole() == staffRole);
                    boolean matchStatus = (status == null) || (s.getStatus() == status);
                    return matchKeyword && matchRole && matchStatus;
                })
                // Thu gom (collect) luồng dữ liệu cuối lưu vào List
                .collect(java.util.stream.Collectors.toList());
    }
}
