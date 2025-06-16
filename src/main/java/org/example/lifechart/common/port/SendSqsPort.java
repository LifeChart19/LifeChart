package org.example.lifechart.common.port;

import org.example.lifechart.domain.notification.entity.Notification;

public interface SendSqsPort {

	void sendNotification(Long userId, Notification.Type type, String title, String message);
	void sendNotification(Long userId, String type, String title, String message);
}
