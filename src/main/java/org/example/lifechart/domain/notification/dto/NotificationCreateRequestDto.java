package org.example.lifechart.domain.notification.dto;

import java.time.LocalDateTime;

import org.example.lifechart.domain.notification.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateRequestDto {

	private Long userId;

	private Notification.Type type;

	private LocalDateTime requestedAt;

	private String title;

	private String message;

}
