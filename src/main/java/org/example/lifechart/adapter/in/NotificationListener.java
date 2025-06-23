package org.example.lifechart.adapter.in;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;

import jakarta.annotation.PreDestroy;
import org.example.lifechart.domain.notification.dto.NotificationCreateRequestDto;
import org.example.lifechart.domain.notification.entity.Notification;
import org.example.lifechart.domain.notification.service.NotificationCreateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

	private final SqsClient sqsClient;

	private final NotificationCreateService service;

	@Value("${aws.url.sqs.notification}")
	private String URL_SQS;

	// @PostConstruct
	// public void startPolling() {
	// 	log.info("aws SQS Listener : {}", URL_SQS);
	//
	// 	Executors.newSingleThreadExecutor().submit(() -> {
	// 		while (!Thread.currentThread().isInterrupted()) {
	// 			try {
	// 				if (sqsClient == null) {
	// 					log.error("SqsClient is null — skipping polling.");
	// 					break;
	// 				}
	//
	// 				ReceiveMessageResponse response = sqsClient.receiveMessage(
	// 						ReceiveMessageRequest.builder()
	// 								.queueUrl(URL_SQS)
	// 								.waitTimeSeconds(20)
	// 								.maxNumberOfMessages(5)
	// 								.messageAttributeNames("All")
	// 								.build());
	//
	// 				for (Message message : response.messages()) {
	// 					try {
	// 						handleMessage(message);
	// 					} catch (DataIntegrityViolationException e) {
	// 						log.warn("이미 처리된 Message : {}", e.getMessage());
	// 					} finally {
	// 						deleteMessage(message);
	// 					}
	// 				}
	// 			} catch (Exception e) {
	// 				log.error("Error in SQS polling : {}", e.toString(), e);
	//
	// 				// Shutdown 된 경우 루프 종료
	// 				if (e.getMessage().contains("Connection pool shut down")) {
	// 					log.error("SQS client connection pool shut down detected — stopping polling loop.");
	// 					break;
	// 				}
	// 			}
	// 		}
	// 	});
	// }

	@PreDestroy
	public void onDestroy() {
		log.info("Shutting down NotificationListener polling");
		// 여기에 Executor shutdown이나 client close 처리하면 좋음
	}


	private void handleMessage(Message message) {
		log.info(message.messageId());
		log.info(message.body());

		var map = message.messageAttributes();

		if (!map.containsKey("queueId")) {
			log.warn("SQS 메시지에 'queueId' attribute 없음. 메시지 ID: {}", message.messageId());
			return; // 무시하거나 fallback 처리
		}

		String queueId = map.get("queueId").stringValue();
		log.info(queueId);

		service.create(parse(queueId, message.body()));
	}

	private void deleteMessage(Message message) {
		sqsClient.deleteMessage(DeleteMessageRequest.builder()
			.queueUrl(URL_SQS)
			.receiptHandle(message.receiptHandle())
			.build());
	}

	// 	{userId}|{type}|{requestedAt}|{title}
	//	123|NOTICE|2025-06-11T07:30:00|WELCOME!
	private NotificationCreateRequestDto parse(String eventId, String message){

		String[] arr = eventId.split("\\|");

		return new NotificationCreateRequestDto(
			Long.valueOf(arr[0]),
			Notification.Type.valueOf(arr[1]),
			LocalDateTime.parse(arr[2]),
			arr[3],
			message
		);

	}

}
