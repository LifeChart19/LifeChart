package org.example.lifechart.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lifechart.domain.user.dto.AccountCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountSnsEventPublisherTest {

    @Mock
    private SnsClient snsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AccountSnsEventPublisher publisher;

    @BeforeEach
    void setUp() {
        TestUtils.setField(publisher, "topicArn", "test-arn");
    }

    @Test
    @DisplayName("SNS 발행 정상 케이스")
    void publishAccountCreatedEvent_success() throws Exception {
        // given
        AccountCreatedEvent event = new AccountCreatedEvent(1L, "email@test.com", "닉네임", "이름", null);
        String json = "{\"userId\":1}";

        given(objectMapper.writeValueAsString(event)).willReturn(json);

        // when
        publisher.publishAccountCreatedEvent(event);

        // then
        verify(objectMapper).writeValueAsString(event);
        verify(snsClient).publish(argThat((PublishRequest req) ->
                req.topicArn().equals("test-arn") && req.message().equals(json)
        ));
    }

    @Test
    @DisplayName("SNS 발행시 직렬화 예외 발생시 런타임 예외 발생")
    void publishAccountCreatedEvent_objectMapperException() throws Exception {
        AccountCreatedEvent event = new AccountCreatedEvent(
                1L,
                "email1@test.com",
                "nickname1",
                "유저이름",
                "2025-06-26T15:00:00"
        );

        when(objectMapper.writeValueAsString(event)).thenThrow(new RuntimeException("직렬화 실패"));

        assertThatThrownBy(() -> publisher.publishAccountCreatedEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SNS 발행 실패");

        verify(objectMapper).writeValueAsString(event);
        verifyNoInteractions(snsClient);
    }

    @Test
    @DisplayName("SNS 발행시 AWS Publish 예외 발생시 런타임 예외 발생")
    void publishAccountCreatedEvent_snsException() throws Exception {
        AccountCreatedEvent event = new AccountCreatedEvent(
                1L,
                "email1@test.com",
                "nickname1",
                "유저이름",
                "2025-06-26T15:00:00"
        );

        String json = "{\"userId\":1}";

        given(objectMapper.writeValueAsString(event)).willReturn(json);
        doThrow(new RuntimeException("AWS 에러")).when(snsClient).publish(any(PublishRequest.class));

        assertThatThrownBy(() -> publisher.publishAccountCreatedEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SNS 발행 실패");

        verify(objectMapper).writeValueAsString(event);
        verify(snsClient).publish(any(PublishRequest.class));
    }
}
