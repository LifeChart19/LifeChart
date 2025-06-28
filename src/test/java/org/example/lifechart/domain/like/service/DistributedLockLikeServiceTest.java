package org.example.lifechart.domain.like.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.like.repository.LikeRepository;
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
class DistributedLockLikeServiceTest {

	@Autowired
	private DistributedLockLikeService distributedLockLikeService;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GoalRepository goalRepository;

	private User savedUser;
	private Goal savedGoal;
	private List<User> userList;

	private void likeLockTest(int numberOfThreads, Runnable action) throws InterruptedException {
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
		userList = new ArrayList<>();
		for (int i = 0; i < 5000; i++) {
			User user = User.builder()
				.name(i + "1")
				.email(i + "1")
				.password(i + "1")
				.nickname(i + "1")
				.isDeleted(false)
				.deletedAt(null)
				.birthDate(LocalDate.of(2000, 3, 5))
				.build();
			savedUser = userRepository.save(user);
			userList.add(user);
		}

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
	@DisplayName("동시에 좋아요를 여러개 생성 & 삭제가 제대로 되는지 확인")
	void likeLock_Ok() throws InterruptedException {

		Map<Long, Long> likeIds = Collections.synchronizedMap(new HashMap<>());


		likeLockTest(5000, () -> {
			long userId;
			synchronized (userList) {
				if (userList.isEmpty()) {
					return;
				}
				int index = userList.size() - 1;
				User removedUser = userList.remove(index);
				userId = removedUser.getId();
			}

			Long likeId = distributedLockLikeService.plusLike(userId, savedGoal.getId()).getId();
			likeIds.put(userId, likeId);
			});

		assertEquals(5000, likeIds.size());
		assertEquals(5000, likeRepository.count());

		likeLockTest(5000, () -> {
			Long userId;
			Long likeId;
			synchronized (likeIds) {
				if (!likeIds.isEmpty()) {
					userId = likeIds.keySet().iterator().next();
					likeId = likeIds.remove(userId);
				} else {
					return;
				}
				distributedLockLikeService.deleteLike(userId, likeId);
			}});

		assertEquals(0, likeIds.size());
		assertEquals(0, likeRepository.count());
	}


}