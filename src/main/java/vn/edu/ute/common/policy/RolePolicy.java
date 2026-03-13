package vn.edu.ute.common.policy;

import vn.edu.ute.common.enumeration.Permission;
import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.security.AuthContext;

public class RolePolicy {

    // --- QUẢN TRỊ CẤU HÌNH HỆ THỐNG ---
    public static boolean canManageSystem() {
        return AuthContext.hasPermission(Permission.MANAGE_SYSTEM);
    }

    // --- QUẢN LÝ NHÂN VIÊN & TÀI KHOẢN ---
    public static boolean canManageStaff() {
        return AuthContext.hasPermission(Permission.MANAGE_STAFF) || AuthContext.hasPermission(Permission.MANAGE_ACCOUNT);
    }

    // --- QUẢN LÝ GIÁO VIÊN & HỒ SƠ ---
    public static boolean canManageProfile() {
        return AuthContext.hasPermission(Permission.MANAGE_TEACHER) || AuthContext.hasPermission(Permission.MANAGE_STUDENT);
    }

    // --- QUẢN LÝ KHÓA HỌC & LỚP HỌC ---
    // Tư vấn viên & Admin có thể chỉnh sửa. Giáo viên chỉ có thể xem.
    public static boolean canEditCourseAndClass() {
        return AuthContext.hasPermission(Permission.MANAGE_COURSE);
    }
    public static boolean canAccessCourseAndClass() {
        return canEditCourseAndClass() || AuthContext.hasRole(Role.Teacher);
    }

    // --- LỊCH TRÌNH & ĐIỂM DANH ---
    public static boolean canAccessSchedule() {
        return canEditCourseAndClass() || AuthContext.hasRole(Role.Teacher);
    }
    // Giáo viên có thể điểm danh. Tư vấn viên/Admin có thể xem/điểm danh.
    public static boolean canAccessAttendance() {
        return AuthContext.hasPermission(Permission.MANAGE_COURSE) || AuthContext.hasRole(Role.Teacher);
    }

    // --- KIỂM TRA ĐẦU VÀO & GHI DANH ---
    // Tư vấn viên xử lý phần này
    public static boolean canAccessEnrollment() {
        return AuthContext.hasPermission(Permission.MANAGE_STUDENT) || AuthContext.hasPermission(Permission.MANAGE_COURSE);
    }

    // --- CHỨNG CHỈ & KẾT QUẢ HỌC TẬP ---
    // Giáo viên có thể nhập điểm. Tư vấn viên có thể cấp chứng chỉ.
    public static boolean canAccessCertification() {
        return AuthContext.hasPermission(Permission.MANAGE_STUDENT) || AuthContext.hasRole(Role.Teacher);
    }
    public static boolean canIssueCertificate() {
        return AuthContext.hasPermission(Permission.MANAGE_STUDENT) || AuthContext.hasPermission(Permission.MANAGE_COURSE);
    }
    public static boolean canEnterScore() {
        return AuthContext.hasRole(Role.Teacher) || AuthContext.hasPermission(Permission.MANAGE_STUDENT);
    }

    // --- THÔNG BÁO ---
    public static boolean canAccessNotification() {
        return AuthContext.isAuthenticated();
    }

    public static boolean canManageNotification() {
        return AuthContext.hasPermission(Permission.MANAGE_SYSTEM) || AuthContext.hasPermission(Permission.MANAGE_COURSE);
    }

    // --- KHUYẾN MÃI & TÀI CHÍNH ---
    public static boolean canAccessPromotion() {
        return AuthContext.hasPermission(Permission.MANAGE_FINANCE) || AuthContext.hasPermission(Permission.MANAGE_COURSE);
    }

    // --- BÁO CÁO ---
    public static boolean canViewReport() {
        return AuthContext.hasPermission(Permission.VIEW_REPORT);
    }

    // --- CỔNG THÔNG TIN HỌC SINH ---
    public static boolean canAccessStudentPortal() {
        return AuthContext.hasRole(Role.Student);
    }
}