package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.TargetRole;
import vn.edu.ute.common.security.AuthContext;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.NotificationRepo;
import vn.edu.ute.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final TransactionManager txManager;

    public NotificationServiceImpl(NotificationRepo notificationRepo, TransactionManager txManager) {
        this.notificationRepo = notificationRepo;
        this.txManager = txManager;
    }

    @Override
    public Notification sendNotification(String title, String content, TargetRole targetRole, UserAccount creator)
            throws Exception {
        return txManager.runInTransaction(em -> {
            if (title == null || title.isBlank()) {
                throw new Exception("Title cannot be empty");
            }
            if (content == null || content.isBlank()) {
                throw new Exception("Content cannot be empty");
            }

            Notification notification = new Notification();
            notification.setTitle(title);
            notification.setContent(content);
            notification.setTargetRole(targetRole);
            notification.setCreatedByUser(creator);

            return notificationRepo.save(em, notification);
        });
    }

    @Override
    public List<Notification> getAllNotifications() throws Exception {
        return txManager.runInTransaction(notificationRepo::findAll);
    }

    @Override
    public List<Notification> getNotificationsForRole(TargetRole targetRole) throws Exception {
        LocalDateTime createdAccount = AuthContext.getCurrentUser().getCreatedAt();
            // Lọc thông báo chỉ lấy những thông báo được tạo sau khi tài khoản được tạo
        return txManager.runInTransaction(em -> notificationRepo.findByTargetRole(em, targetRole))
                .stream()
                .filter(n -> n.getCreatedAt().isAfter(createdAccount))
                .toList();
    }

    @Override
    public void deleteNotification(Long id) throws Exception {
        txManager.runInTransactionVoid(em -> {
            notificationRepo.delete(em, id);
            return null;
        });
    }
}
