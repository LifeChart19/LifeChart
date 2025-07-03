// package org.example.lifechart.adapter.in;
//
// import java.util.Optional;
//
// import org.example.lifechart.domain.comment.repository.CommentRepository;
// import org.example.lifechart.domain.comment.service.DistributedLockCommentService;
// import org.example.lifechart.domain.goal.event.GoalDeletedEvent;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.event.TransactionPhase;
// import org.springframework.transaction.event.TransactionalEventListener;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class CommentCleanupListener {
//
// 	private final DistributedLockCommentService lockCommentService;
// 	private final CommentRepository commentRepository;
//
// 	@Async
// 	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
// 	public void handle(GoalDeletedEvent event) {
// 		try {
// 			Optional<Long> commentId = commentRepository.findByUserId(event.getUserId(), event.getGoalId());
// 			if (commentId.isEmpty()) {
// 				log.warn("유저의 comment가 존재하지 않습니다 - userId: {}", event.getUserId());
// 			} else {
// 				lockCommentService.deleteCommentByGoal(event.getUserId(), commentId.get());
// 			}
// 		} catch (Exception e) {
// 			log.warn("댓글 삭제 실패 - goalId: {}", event.getGoalId());
// 		}
//
// 	}
// }
