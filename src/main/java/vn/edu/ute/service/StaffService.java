package vn.edu.ute.service;

import vn.edu.ute.model.Staff;

public interface StaffService {
    Staff createStaffAccount(Staff staff, String username, String initialPassword,
            vn.edu.ute.common.enumeration.Role userRole) throws Exception;

    java.util.List<Staff> getAllStaffs() throws Exception;

    Staff updateStaff(Staff staff) throws Exception;

    void deleteStaff(Long id) throws Exception;

    java.util.List<Staff> filterStaffs(String keyword, vn.edu.ute.common.enumeration.StaffRole staffRole,
            vn.edu.ute.common.enumeration.Status status) throws Exception;
}
