package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;
import vn.edu.ute.service.AuthService;
import vn.edu.ute.common.util.PasswordUtil;
import vn.edu.ute.common.enumeration.Role;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepo;
    private final TransactionManager txManager;

    public AuthServiceImpl(UserAccountRepository userAccountRepo, TransactionManager txManager) {
        this.userAccountRepo = userAccountRepo;
        this.txManager = txManager;
    }

    @Override
    public UserAccount login(String username, String password) throws Exception {
        return txManager.runInTransaction(em -> {
            Optional<UserAccount> accountOpt = userAccountRepo.findByUsername(em, username);
            
            return accountOpt.filter(account -> account.getIsActive() != null && account.getIsActive())
                             .filter(account -> PasswordUtil.checkPassword(password, account.getPasswordHash()))
                             .orElseThrow(() -> new Exception("Invalid username or password, or account disabled."));
        });
    }

    @Override
    public UserAccount register(String username, String password, Role role) throws Exception {
        return txManager.runInTransaction(em -> {
            boolean exists = userAccountRepo.existsByUsername(em, username);
            if (exists) {
                throw new Exception("Username already exists: " + username);
            }

            UserAccount newAccount = new UserAccount();
            newAccount.setUsername(username);
            newAccount.setPasswordHash(PasswordUtil.hashPassword(password));
            newAccount.setRole(role);
            newAccount.setIsActive(true);

            return userAccountRepo.save(em, newAccount);
        });
    }
}
