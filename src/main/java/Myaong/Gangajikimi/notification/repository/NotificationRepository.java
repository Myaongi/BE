package Myaong.Gangajikimi.notification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import Myaong.Gangajikimi.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
}