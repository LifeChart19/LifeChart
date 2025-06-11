package org.example.lifechart.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.domain.awsTest.AwsTestPort;
import org.example.lifechart.domain.notification.dto.MessageQueueDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsTestAdapter implements AwsTestPort {

    private final SqsClient sqsClient;
    private final SnsClient snsClient;

    @Value("${aws.url.sqs.test}")
    private String URL_SQS_TEST;

    @Value("${aws.arn.sns.test}")
    private String ARN_SNS_TEST;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String sendSQS() {

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now.truncatedTo(ChronoUnit.SECONDS), ZoneId.systemDefault());

        var message = new MessageQueueDto(
                "1-NOTIFY-" + ldt + "-testTitle",
                now.toString());


        SendMessageRequest msgReq = null;
        try {
            msgReq = SendMessageRequest.builder()
                    .queueUrl(URL_SQS_TEST)
                    .messageBody(objectMapper.writeValueAsString(message))
                    .build();
        } catch (JsonProcessingException e) {
            log.info("failed to Serialize SQS message : {}", message, e);
        }

        SendMessageResponse msgRes = sqsClient.sendMessage(msgReq);

        return msgRes.toString();

    }

    @Override
    public String sendSNS() {

        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now.truncatedTo(ChronoUnit.SECONDS), ZoneId.systemDefault());

        var message = new MessageQueueDto(
                "1-NOTIFY-" + ldt + "-testTitle",
                now.toString());

        PublishRequest pub = null;
        try {
            pub = PublishRequest.builder()
                    .topicArn(ARN_SNS_TEST)
                    .message(objectMapper.writeValueAsString(message))
                    .build();
        } catch (JsonProcessingException e) {
            log.info("failed to Serialize SQS message : {}", message, e);
        }

        PublishResponse pubRes = snsClient.publish(pub);

        return pubRes.toString();
    }

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

            DeleteMessageResponse deleteResponse = sqsClient.deleteMessage(deleteRequest);

            log.info("Delete status: {}", deleteResponse.sdkHttpResponse().statusCode());
        }

        return dto;
    }
}
