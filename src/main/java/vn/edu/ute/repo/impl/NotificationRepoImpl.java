package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.common.enumeration.TargetRole;
import vn.edu.ute.model.Notification;
import vn.edu.ute.repo.NotificationRepo;

import java.util.List;
import java.util.Optional;

public class NotificationRepoImpl implements NotificationRepo {
    @Override
    public Notification save(EntityManager em, Notification notification) {
        if (notification.getNotificationId() == null) {
            em.persist(notification);
            return notification;
        } else {
            return em.merge(notification);
        }
    }

    @Override
    public Optional<Notification> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Notification.class, id));
    }

    @Override
    public List<Notification> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT n FROM Notification n JOIN FETCH n.createdByUser ORDER BY n.createdAt DESC",
                Notification.class
        ).getResultList();
    }

    @Override
            public List<Notification> findByTargetRole(EntityManager em, TargetRole targetRole) {
        return em.createQuery(
                "SELECT n FROM Notification n LEFT JOIN FETCH n.createdByUser WHERE n.targetRole = :role OR n.targetRole = 'All' ORDER BY n.createdAt DESC",
                Notification.class)
                .setParameter("role", targetRole)
                .getResultList();
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Notification n = em.find(Notification.class, id);
        if (n != null) {
            em.remove(n);
        }
    }
}
