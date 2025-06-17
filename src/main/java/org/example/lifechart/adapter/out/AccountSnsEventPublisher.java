package org.example.lifechart.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.dynamic.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.port.AccountEventPublisherPort;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

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
            throw new RuntimeException("SNS 발행 실패", e);
        }
    }
}

