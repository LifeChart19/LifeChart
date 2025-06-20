package org.example.lifechart.domain.notification.dto;

import java.time.LocalDateTime;

import org.example.lifechart.domain.notification.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
	private Long id;

	private Notification.Type type;

	private String title;

	private String message;

	private LocalDateTime completedAt;

	private LocalDateTime requestedAt;

	public NotificationResponseDto(Notification n){
		this.id = n.getId();
		this.type = n.getType();
		this.title = n.getTitle();
		this.message = n.getMessage();
		this.completedAt = n.getCompletedAt();
		this.requestedAt = n.getRequestedAt();
	}
}
