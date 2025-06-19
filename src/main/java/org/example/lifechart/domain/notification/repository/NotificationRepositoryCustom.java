package org.example.lifechart.domain.notification.repository;

import java.util.List;

import org.example.lifechart.domain.notification.dto.NotificationResponseDto;

public interface NotificationRepositoryCustom {

	List<NotificationResponseDto> getList(Long userId, Long cursor, int size);
}
