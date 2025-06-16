package org.example.lifechart.adapter.out;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

	@Value("${aws.url.sqs.notification}")
	private String URL_SQS;

	@Override
	public void sendNotification(Long userId, Notification.Type type, String title, String message) {

		StringBuilder st = new StringBuilder();

		st.append(userId.toString()).append('|')
			.append(type).append('|')
			.append(LocalDateTime.now().withNano(0)).append('|')
			.append(title);

		Map<String, MessageAttributeValue> attr = new HashMap<>();

		attr.put("queueId", MessageAttributeValue.builder()
			.dataType("String")
			.stringValue(st.toString())
			.build());

		SendMessageRequest msgReq = null;

		msgReq = SendMessageRequest.builder()
			.queueUrl(URL_SQS)
			.messageBody(message)
			.messageAttributes(attr)
			.build();

		SendMessageResponse msgRes = sqsClient.sendMessage(msgReq);

	}

	@Override
	public void sendNotification(Long userId, String type, String title, String message) {
		sendNotification(userId, Notification.Type.valueOf(type), title, message);
	}
}
