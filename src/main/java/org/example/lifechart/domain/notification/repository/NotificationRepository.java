package org.example.lifechart.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.lifechart.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findTop20ByUserIdOrderByRequestedAtDesc(Long userId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Notification n SET n.completedAt=:now WHERE n.userId = :userId AND n.completedAt IS NULL")
	void patchAllByUserId(Long userId, LocalDateTime now);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Notification n SET n.completedAt=:now WHERE n.id = :notificationId AND n.userId = :userId AND n.completedAt IS NULL")
	void patchByUserId(Long notificationId, Long userId, LocalDateTime now);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Notification n SET n.fetchedAt=:now WHERE n.id = :notificationId AND n.userId = :userId AND n.fetchedAt IS NULL")
	void fetchedByUserId(Long notificationId, Long userId, LocalDateTime now);

}
