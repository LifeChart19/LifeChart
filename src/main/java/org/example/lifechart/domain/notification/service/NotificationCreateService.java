package org.example.lifechart.domain.notification.service;

import org.example.lifechart.domain.notification.dto.NotificationCreateRequestDto;
import org.example.lifechart.domain.notification.entity.Notification;
import org.example.lifechart.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationCreateService {

	private final NotificationRepository notificationRepo;

	@Transactional
	public void create(NotificationCreateRequestDto dto){

		Notification notification = new Notification(dto);
		notificationRepo.save(notification);

	}


}
