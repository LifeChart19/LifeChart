package org.example.lifechart.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.user.port.AccountEventPublisherPort;
import org.example.lifechart.domain.user.dto.AccountCreatedEvent;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccountSnsEventPublisher implements AccountEventPublisherPort {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.account-topic-arn}")
    private String topicArn;

    @Override
    public void publishAccountCreatedEvent(AccountCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(json)
                    .build());
        } catch (Exception e) {
            log.error("SNS 발행 실패 - userId={}, email={}", event.getUserId(), event.getEmail(), e);
            throw new RuntimeException("SNS 발행 실패", e);
        }
    }
}

