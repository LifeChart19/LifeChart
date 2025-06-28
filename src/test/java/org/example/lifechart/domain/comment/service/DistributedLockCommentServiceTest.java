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
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

		try {
			for (int i = 0; i < numberOfThreads; i++) {
				executorService.submit(() -> {
					try {
						action.run();
					} catch (Exception e) {
						System.out.println("예외 발생: " + e.getMessage());
					} finally {
						latch.countDown();
					}
				});
			}
			latch.await();
		} finally {
			executorService.shutdown();
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
		List<Long> commentIds = Collections.synchronizedList(new ArrayList<>());


		commentLockTest(5000, ()-> {
			Long id = distributedLockCommentService.createComment(savedUser.getId(), savedGoal.getId(),
				commentRequestDto).getId();
			commentIds.add(id);
			});


		assertEquals(5000, commentIds.size());
		assertEquals(5000, commentRepository.count());


		commentLockTest(5000, () -> {
			Long id;
			synchronized (commentIds) {
				if (!commentIds.isEmpty()) {
					id = commentIds.remove(0);
				} else {
					return;
				}
			}
			distributedLockCommentService.deleteComment(savedUser.getId(), id);
			});

		assertEquals(0, commentIds.size());
		assertEquals(0, commentRepository.count());
	}
}