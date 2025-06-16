package org.example.lifechart.domain.notification.dto;

import java.time.LocalDateTime;

import org.example.lifechart.domain.notification.entity.Notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponseDto {
	private Long id;

	private Notification.Type type;

	private Notification.Status status;

	private String title;

	private String message;

	private LocalDateTime completedAt;

	public NotificationResponseDto(Notification n){
		this.id = n.getId();
		this.type = n.getType();
		this.status = n.getStatus();
		this.title = n.getTitle();
		this.message = n.getMessage();
		this.completedAt = n.getCompletedAt();
	}
}
