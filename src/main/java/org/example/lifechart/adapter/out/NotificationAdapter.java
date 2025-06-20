package org.example.lifechart.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.domain.notification.dto.MessageQueueDto;
import org.example.lifechart.domain.notification.port.NotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationAdapter implements NotificationPort {

    private final SqsClient sqsClient;

    @Value("${aws.url.sqs.notification}")
    private String URL_SQS_TEST;

    public MessageQueueDto receiveAndDelete() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(URL_SQS_TEST)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10)
                .messageAttributeNames("All")
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
        MessageQueueDto dto = null;

        for (Message m : messages) {
            String body = m.body(); // 실제 message 본문
            String queueId = m.messageAttributes().get("queueId").stringValue();

            String[] parts = queueId.split("\\|");
            if (parts.length < 4) {
                log.warn("Invalid queueId format: {}", queueId);
                continue;
            }

            Long userId = Long.parseLong(parts[0]);
            String type = parts[1];
            String createdAt = parts[2];
            String title = parts[3];

            log.info("SQS 수신: userId={}, type={}, title={}, createdAt={}", userId, type, title, createdAt);

            dto = new MessageQueueDto(type, body);

            // 처리 완료 후 삭제
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(URL_SQS_TEST)
                    .receiptHandle(m.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteRequest);
        }

        return dto;
    }

    private void startPolling(){};

}
