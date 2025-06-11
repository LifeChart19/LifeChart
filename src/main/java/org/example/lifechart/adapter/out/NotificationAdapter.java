package org.example.lifechart.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws.url.sqs.test}")
    private String URL_SQS_TEST;

    public MessageQueueDto receiveAndDelete() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(URL_SQS_TEST)
                .maxNumberOfMessages(5) // 한 번에 최대 5개
                .waitTimeSeconds(10)    // Long Polling
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
        MessageQueueDto dto = null;

        for (Message m : messages) {
            System.out.println("Received: " + m.body());

            String body = m.body();


            try {
                dto = objectMapper.readValue(body, MessageQueueDto.class);
            } catch (JsonProcessingException e) {
                log.info("failed to parse SQS message : {}", body, e);
            }

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
