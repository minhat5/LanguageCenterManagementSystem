package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.repo.UserAccountRepo;
import vn.edu.ute.model.UserAccount;
import java.util.Optional;

public class UserAccountRepoImpl implements UserAccountRepo {

    @Override
    public Optional<vn.edu.ute.model.UserAccount> findByUsername(EntityManager em, String username) {
        return em.createQuery("SELECT u FROM UserAccount u " +
                "LEFT JOIN FETCH u.staff " +
                "LEFT JOIN FETCH u.teacher " +
                "LEFT JOIN FETCH u.student " +
                "WHERE u.username = :username", UserAccount.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public vn.edu.ute.model.UserAccount save(EntityManager em, vn.edu.ute.model.UserAccount userAccount) {
        if (userAccount.getUserId() == null) {
            em.persist(userAccount);
            return userAccount;
        } else {
            return em.merge(userAccount);
        }
    }

    @Override
    public boolean existsByUsername(EntityManager em, String username) {
        Long count = em.createQuery("SELECT count(u) FROM UserAccount u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}
