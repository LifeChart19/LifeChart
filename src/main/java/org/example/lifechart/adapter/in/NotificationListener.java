package org.example.lifechart.adapter.in;

//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class NotificationListener {
//
//	private final SqsClient sqsClient;
//
//	private final NotificationCreateService service;
//
//	@Value("${aws.url.sqs.notification}")
//	private String URL_SQS;
//
//	@PostConstruct
//	public void startPolling() {
//		Executors.newSingleThreadExecutor().submit(() -> {
//			while (true) {
//				try {
//					ReceiveMessageResponse response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
//						.queueUrl(URL_SQS)
//						.waitTimeSeconds(20)  // long polling
//						.maxNumberOfMessages(5)
//						.messageAttributeNames("All")
//						.build());
//
//					for (Message message : response.messages()) {
//						try{
//							handleMessage(message);
//						} catch (DataIntegrityViolationException e){
//							log.warn("이미 처리된 Message : {}", e.getMessage());
//						} finally {
//							deleteMessage(message);
//						}
//					}
//				} catch (Exception e) {
//					log.error("Error in SQS polling : {}", e.getMessage());
//				}
//			}
//		});
//	}
//
//	private void handleMessage(Message message) {
//		log.info(message.messageId());
//		log.info(message.body());
//
//		var map = message.messageAttributes();
//
//		String body = message.body();
//		String queueId = map.get("queueId").stringValue();
//		log.info(queueId);
//
//		service.create(parse(queueId, body));
//
//	}
//
//	private void deleteMessage(Message message) {
//		sqsClient.deleteMessage(DeleteMessageRequest.builder()
//			.queueUrl(URL_SQS)
//			.receiptHandle(message.receiptHandle())
//			.build());
//	}
//
//	// 	{userId}|{type}|{requestedAt}|{title}
//	//	123|NOTICE|2025-06-11T07:30:00|WELCOME!
//	private NotificationCreateRequestDto parse(String eventId, String message){
//
//		String[] arr = eventId.split("\\|");
//
//		return new NotificationCreateRequestDto(
//			Long.valueOf(arr[0]),
//			Notification.Type.valueOf(arr[1]),
//			LocalDateTime.parse(arr[2]),
//			arr[3],
//			message
//		);
//
//	}
//
//}
