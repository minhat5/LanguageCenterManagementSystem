package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.UserAccount;

import java.util.Optional;

public interface UserAccountRepo {
    Optional<UserAccount> findByUsername(EntityManager em, String username);
    UserAccount save(EntityManager em, UserAccount userAccount);
    boolean existsByUsername(EntityManager em, String username);
}
