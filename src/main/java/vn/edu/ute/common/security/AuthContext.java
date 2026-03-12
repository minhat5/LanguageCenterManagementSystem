package vn.edu.ute.common.security;

import vn.edu.ute.common.enumeration.Permission;
import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.StaffRole;
import vn.edu.ute.model.UserAccount;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AuthContext {
    private static UserAccount currentUser;
    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new EnumMap<>(Role.class);
    private static final Map<StaffRole, Set<Permission>> STAFF_ROLE_PERMISSIONS = new EnumMap<>(StaffRole.class);

    static {
        // Init permissions for Admin: full access
        ROLE_PERMISSIONS.put(Role.Admin, EnumSet.allOf(Permission.class));
        ROLE_PERMISSIONS.put(Role.Teacher, EnumSet.noneOf(Permission.class));
        ROLE_PERMISSIONS.put(Role.Student, EnumSet.noneOf(Permission.class));
        ROLE_PERMISSIONS.put(Role.Staff, EnumSet.noneOf(Permission.class));

        // Init permissions for Manager: high-level access but might not manage
        // accounts/finance
        STAFF_ROLE_PERMISSIONS.put(StaffRole.Manager, EnumSet.of(
                Permission.MANAGE_SYSTEM,
                Permission.MANAGE_STAFF,
                Permission.MANAGE_TEACHER,
                Permission.MANAGE_STUDENT,
                Permission.MANAGE_COURSE,
                Permission.VIEW_REPORT));

        // Init permissions for Accountant: financial and related student data
        STAFF_ROLE_PERMISSIONS.put(StaffRole.Accountant, EnumSet.of(
                Permission.MANAGE_FINANCE,
                Permission.MANAGE_STUDENT,
                Permission.VIEW_REPORT));

        // Init permissions for Consultant: enrollment, student support, courses
        STAFF_ROLE_PERMISSIONS.put(StaffRole.Consultant, EnumSet.of(
                Permission.MANAGE_STUDENT,
                Permission.MANAGE_COURSE));

        // Init permissions for Other: minimal or no specific module access
        STAFF_ROLE_PERMISSIONS.put(StaffRole.Other, EnumSet.noneOf(Permission.class));
    }

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean hasPermission(Permission permission) {
        if (!isAuthenticated()) {
            return false;
        }
        Role currentRole = currentUser.getRole();
        Set<Permission> permissions = ROLE_PERMISSIONS.get(currentRole);
        if (permissions != null && permissions.contains(permission)) {
            return true;
        }

        if (currentRole == Role.Staff && currentUser.getStaff() != null) {
            Set<Permission> staffPermissions = STAFF_ROLE_PERMISSIONS.get(currentUser.getStaff().getStaffRole());
            return staffPermissions != null && staffPermissions.contains(permission);
        }
        return false;
    }

    public static boolean hasRole(Role role) {
        if (!isAuthenticated()) {
            return false;
        }
        return currentUser.getRole() == role;
    }
}
