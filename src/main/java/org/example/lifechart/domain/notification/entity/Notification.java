package org.example.lifechart.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import org.example.lifechart.domain.notification.dto.NotificationCreateRequestDto;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"type", "user_id", "requested_at", "title"})
	}
)
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@Enumerated(EnumType.STRING)
	private Type type;

	@Enumerated(EnumType.STRING)
	private Status status;

	private String title;

	private String message;

	@Column(columnDefinition = "DATETIME(0)")
	private LocalDateTime requestedAt;

	private LocalDateTime processedAt;

	private LocalDateTime completedAt;

	private LocalDateTime fetchedAt;

	public Notification(NotificationCreateRequestDto dto) {
		this.userId = dto.getUserId();
		this.type = dto.getType();
		this.requestedAt = dto.getRequestedAt();
		this.title = dto.getTitle();
		this.message = dto.getMessage();
		this.processedAt = LocalDateTime.now();
		this.status = Status.UNREAD;
	}

	public String getEventId() {
		return userId + "-" +
			type + "-" +
			requestedAt.toString() + "-" +
			title;
	}

	public enum Type {
		NOTICE, EMAIL, USER_NOTIFICATION
	}

	public enum Status {
		FAILED, UNREAD, READ
	}
}