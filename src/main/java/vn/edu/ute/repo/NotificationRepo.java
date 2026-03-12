package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.common.enumeration.TargetRole;
import vn.edu.ute.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepo {
    Notification save(EntityManager em, Notification notification);

    Optional<Notification> findById(EntityManager em, Long id);

    List<Notification> findAll(EntityManager em);

    List<Notification> findByTargetRole(EntityManager em, TargetRole targetRole);

    void delete(EntityManager em, Long id);
}
