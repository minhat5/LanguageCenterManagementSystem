package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.TargetRole;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(String title, String content, TargetRole targetRole, UserAccount creator)
            throws Exception;

    List<Notification> getAllNotifications() throws Exception;

    List<Notification> getNotificationsForRole(TargetRole targetRole) throws Exception;

    void deleteNotification(Long id) throws Exception;
}
