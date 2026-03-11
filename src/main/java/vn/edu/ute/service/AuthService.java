package vn.edu.ute.service;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.common.enumeration.Role;

public interface AuthService {
    UserAccount login(String username, String password) throws Exception;
    UserAccount register(String username, String password, Role role) throws Exception;
}
