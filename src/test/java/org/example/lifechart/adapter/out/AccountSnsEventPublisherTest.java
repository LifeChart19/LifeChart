package org.example.lifechart.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lifechart.domain.user.dto.AccountCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import static org.mockito.Mockito.*;



class AccountSnsEventPublisherTest {

    @Mock
    private SnsClient snsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AccountSnsEventPublisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // topicArn 세팅 (Reflection, 테스트 목적으로)
        TestUtils.setField(publisher, "topicArn", "test-arn");
    }

    @Test
    void publishAccountCreatedEvent_정상호출() throws Exception {
        // given
        AccountCreatedEvent event = new AccountCreatedEvent(1L, "email@test.com", "닉네임", "이름", null);
        String json = "{\"userId\":1}";

        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        // when
        publisher.publishAccountCreatedEvent(event);

        // then
        verify(objectMapper).writeValueAsString(event);
        verify(snsClient).publish(argThat((PublishRequest req) ->
                req.topicArn().equals("test-arn") && req.message().equals(json)
        ));
    }

    @Test
    void publishAccountCreatedEvent_예외시_런타임예외_발생() throws Exception {
        // given
        AccountCreatedEvent event = new AccountCreatedEvent(1L, "email@test.com", "닉네임", "이름", null);

        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("직렬화 실패"));

        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        publisher.publishAccountCreatedEvent(event)
                ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SNS 발행 실패");

        verify(objectMapper).writeValueAsString(event);
        verify(snsClient, never()).publish(any(PublishRequest.class));
    }
}
