package org.example.lifechart.domain.awsTest;


import org.example.lifechart.domain.notification.dto.MessageQueueDto;

public interface AwsTestPort {
    String sendSQS();
    String sendSNS();
    MessageQueueDto receiveAndDelete();
}
