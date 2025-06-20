package org.example.lifechart.domain.notification.repository;

import java.util.List;

import org.example.lifechart.domain.notification.dto.NotificationResponseDto;
import org.example.lifechart.domain.notification.entity.Notification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public List<NotificationResponseDto> getList(Long userId, Long cursor, int size) {

		List<NotificationResponseDto> result = em.createQuery(
			"SELECT new org.example.lifechart.domain.notification.dto.NotificationResponseDto(n.id, n.type, n.title, n.message, n.completedAt, n.requestedAt) "
				+ "FROM Notification n WHERE n.id <= :cursor AND n.userId = :userId ORDER BY n.id DESC", NotificationResponseDto.class
		).setParameter("userId", userId)
			.setParameter("cursor", cursor)
			.setMaxResults(size)
			.getResultList();

		return result;
	}
}
