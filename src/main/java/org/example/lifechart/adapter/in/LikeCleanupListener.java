// package org.example.lifechart.adapter.in;
//
// import java.util.Optional;
//
// import org.example.lifechart.domain.goal.event.GoalDeletedEvent;
// import org.example.lifechart.domain.like.repository.LikeRepository;
// import org.example.lifechart.domain.like.service.DistributedLockLikeService;
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
// public class LikeCleanupListener {
//
// 	private final DistributedLockLikeService lockLikeService;
// 	private final LikeRepository likeRepository;
//
// 	@Async
// 	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
// 	public void handle(GoalDeletedEvent event) {
// 		try {
// 			Optional<Long> likeId = likeRepository.findByUserId(event.getUserId(), event.getGoalId());
// 			if (likeId.isEmpty()) {
// 				log.warn("유저의 like가 존재하지 않습니다 - userId:{}", event.getUserId());
// 			} else {
// 				lockLikeService.deleteLikeByGoal(event.getUserId(), likeId.get());
// 			}
// 		} catch (Exception e) {
// 			log.warn("좋아요 삭제 실패 - goalId: {}", event.getGoalId());
// 		}
//
// 	}
// }
