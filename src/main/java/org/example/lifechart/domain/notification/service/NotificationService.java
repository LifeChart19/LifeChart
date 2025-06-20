package org.example.lifechart.domain.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.notification.dto.NotificationResponseDto;
import org.example.lifechart.domain.notification.entity.Notification;
import org.example.lifechart.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepo;

	@Transactional
	public List<NotificationResponseDto> getList(
		Long userId, Long cursor, int size
	) {
		if(cursor == null) cursor = Long.MAX_VALUE;
		if(size > 20) size = 20;

		return notificationRepo.getList(
				userId, cursor, size);
	}

	@Transactional
	public void patchAll(
		Long userId
	) {
		notificationRepo.patchAllByUserId(userId, LocalDateTime.now());
	}

	@Transactional
	public NotificationResponseDto get(Long notificationId, Long userId) {

		Notification n = notificationRepo.findById(notificationId)
			.orElseThrow(
				() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND)
			);

		if (!Objects.equals(n.getUserId(), userId)) {
			throw new CustomException(ErrorCode.NOTIFICATION_PERMISSION);
		}

		notificationRepo.fetchedByUserId(n.getId(), userId, LocalDateTime.now());

		return new NotificationResponseDto(n);
	}

	@Transactional
	public void patch(Long notificationId, Long userId) {
		notificationRepo.patchByUserId(notificationId, userId, LocalDateTime.now());
	}

}
