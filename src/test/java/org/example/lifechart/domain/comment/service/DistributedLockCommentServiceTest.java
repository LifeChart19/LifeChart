package org.example.lifechart.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.repository.CommentRepository;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DistributedLockCommentServiceTest {

	@Autowired
	private  CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GoalRepository goalRepository;

	@Autowired
	private DistributedLockCommentService distributedLockCommentService;

	private User savedUser;
	private Goal savedGoal;


	private void commentLockTest(int numberOfThreads, Runnable action) throws InterruptedException {

		// (값) 만큼의 쓰레드 풀 생성과 동시에 병렬로 실행되게 해줌
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

		// 모든 쓰레드가 작업을 다할 때까지 기다리게 해주는 도구
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		try {
			for (int i = 0; i < numberOfThreads; i++) {
				executorService.submit(() -> { // 쓰레드 풀에 작업 제출(비동기)
					try {
						action.run(); // 동시성 테스트 하려는 로직
					} catch (Exception e) {
						System.out.println("예외 발생: " + e.getMessage());
					} finally {
						latch.countDown(); // 하나의 쓰레드 작업이 끝났을 때 1씩 감소
					}
				});
			}
			latch.await(); // 모든 쓰레드의 작업이 완료될 때까지 대기하고 모두 완료되면 다음 단계로
		} finally {
			executorService.shutdown(); // 자원 해제
		}
	}

	@BeforeEach
	void setUp() {
		User user = User.builder()
			.name("1")
			.email("1")
			.password("1")
			.nickname("1")
			.isDeleted(false)
			.deletedAt(null)
			.birthDate(LocalDate.of(2000, 3, 5))
			.build();
		savedUser = userRepository.save(user);
		Goal goal = Goal.builder()
			.user(savedUser)
			.title("안녕")
			.category(Category.ETC)
			.targetAmount(1L)
			.startAt(LocalDateTime.now())
			.endAt(LocalDateTime.now().plusYears(5))
			.status(Status.ACTIVE)
			.share(Share.ALL)
			.tags(List.of("안녕"))
			.build();
		savedGoal = goalRepository.save(goal);
	}

	@Test
	@DisplayName("동시에 댓글을 여러개 생성 & 삭제가 제대로 되는지 확인")
	void commentLock_Ok() throws InterruptedException {

		CommentRequestDto commentRequestDto = CommentRequestDto.builder().contents("1").build();

		// 멀티쓰레드 환경에서 안전하게 추가/ 삭제 가능한 동시성 제어 리스트
		List<Long> commentIds = Collections.synchronizedList(new ArrayList<>());


		commentLockTest(5000, ()-> {
			Long commentId = distributedLockCommentService.createComment(savedUser.getId(), savedGoal.getId(),
				commentRequestDto).getId();
			commentIds.add(commentId);
		});

		assertEquals(5000, commentIds.size());
		assertEquals(5000, commentRepository.count());


		commentLockTest(5000, () -> {
			Long commentId;

			// 간단한 add / remove면 동시성 제어 리스트로 되지만 isEmpty()와 remove가 묶여 복합적일 땐 에러가 날 위험이 있어서
			// 명시적 동기화 추가
			synchronized (commentIds) {
				if (!commentIds.isEmpty()) {
					commentId = commentIds.remove(0);
				} else {
					return;
				}
			}
			distributedLockCommentService.deleteComment(savedUser.getId(), commentId);
		});

		assertEquals(0, commentIds.size());
		assertEquals(0, commentRepository.count());
	}
}