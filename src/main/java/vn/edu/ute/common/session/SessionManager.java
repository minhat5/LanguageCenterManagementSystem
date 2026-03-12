package vn.edu.ute.common.session;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.common.security.AuthContext;

/**
 * Session Manager to hold state application session.
 * Connects with AuthContext to manage User Session.
 */
public class SessionManager {

    public static void login(UserAccount user) {
        AuthContext.setCurrentUser(user);
    }

    public static void logout() {
        AuthContext.logout();
    }

    public static UserAccount getCurrentUser() {
        return AuthContext.getCurrentUser();
    }

    public static boolean isUserLoggedIn() {
        return AuthContext.isAuthenticated();
    }
}
