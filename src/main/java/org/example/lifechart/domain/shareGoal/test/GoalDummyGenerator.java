// package org.example.lifechart.domain.shareGoal.test;
//
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.example.lifechart.domain.goal.entity.Goal;
// import org.example.lifechart.domain.goal.enums.Category;
// import org.example.lifechart.domain.goal.enums.Share;
// import org.example.lifechart.domain.goal.enums.Status;
// import org.example.lifechart.domain.goal.repository.GoalRepository;
// import org.example.lifechart.domain.user.entity.User;
// import org.example.lifechart.domain.user.repository.UserRepository;
// import org.springframework.boot.ApplicationArguments;
// import org.springframework.boot.ApplicationRunner;
// import org.springframework.stereotype.Component;
//
// import lombok.RequiredArgsConstructor;
//
// @Component
// @RequiredArgsConstructor
// public class GoalDummyGenerator implements ApplicationRunner {
//
// 	private final GoalRepository goalRepository;
// 	private final UserRepository userRepository;
//
// 	@Override
// 	public void run(ApplicationArguments args) {
// 		User user = userRepository.findById(1L).orElseThrow();
//
// 		List<Goal> goals = new ArrayList<>();
// 		for (int i = 1; i <= 100000; i++) {
// 			goals.add(Goal.builder()
// 				.user(user)
// 				.title("은퇴 하고 싶다")
// 				.category(Category.HOUSING)
// 				.targetAmount(10000L)
// 				.share(Share.ALL)
// 				.status(Status.ACTIVE)
// 				.startAt(LocalDateTime.now())
// 				.endAt(LocalDateTime.now().plusDays(7))
// 				.category(Category.RETIREMENT)
// 				.tags(List.of("은퇴"))
// 				.commentCount(5)
// 				.likeCount(5)
// 				.build());
// 		}
//
// 		goalRepository.saveAll(goals);
// 	}
// }
