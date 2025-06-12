package org.example.lifechart.domain.notification.port;

import org.example.lifechart.domain.notification.dto.MessageQueueDto;

public interface NotificationPort {

    MessageQueueDto receiveAndDelete();

}
