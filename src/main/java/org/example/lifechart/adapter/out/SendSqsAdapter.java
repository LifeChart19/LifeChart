package org.example.lifechart.adapter.out;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lifechart.common.port.SendSqsPort;
import org.example.lifechart.domain.notification.entity.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Component
@RequiredArgsConstructor
public class SendSqsAdapter implements SendSqsPort {

	private final SqsClient sqsClient;
	private final ObjectMapper objectMapper;

	@Value("${aws.url.sqs.notification}")
	private String URL_SQS;

	@Override
	public void sendNotification(Long userId, Notification.Type type, String title, String message) {
		try {
			// 메타 정보
			String queueId = userId + "|" + type + "|" + LocalDateTime.now().withNano(0) + "|" + title;

			Map<String, MessageAttributeValue> attr = new HashMap<>();
			attr.put("queueId", MessageAttributeValue.builder()
					.dataType("String")
					.stringValue(queueId)
					.build());

			// 본문을 JSON으로 wrapping
			Map<String, String> jsonBody = new HashMap<>();
			jsonBody.put("type", type.name());
			jsonBody.put("title", title);
			jsonBody.put("message", message);

			String body = objectMapper.writeValueAsString(jsonBody);

			SendMessageRequest msgReq = SendMessageRequest.builder()
					.queueUrl(URL_SQS)
					.messageAttributes(attr)
					.messageBody(body)
					.build();

			SendMessageResponse response = sqsClient.sendMessage(msgReq);

		} catch (Exception e) {
			throw new RuntimeException("SQS 메시지 전송 실패", e);
		}
	}
	@Override
	public void sendNotification(Long userId, String type, String title, String message) {
		sendNotification(userId, Notification.Type.valueOf(type), title, message);
	}
}
