package org.example.lifechart.domain.shareGoal.service;

import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.shareGoal.dto.reqeust.ShareGoalSearchRequestDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalSearchResponseDto;
import org.example.lifechart.domain.shareGoal.enums.Sort;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareGoalServiceImpl implements ShareGoalService {
	private final GoalRepository goalRepository;
	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	@Override
	public ShareGoalCursorResponseDto getShareGoals(
		Long authId, Long cursorId, int size, Category category, Share share, Sort sort, Period period
	) {

		User foundUser = validUser(authId);

		// cursorGoal의 commentCount + likeCount한 값이 CustomShareGoalRepositoryImpl에서 비교하는데 필요
		Goal cursorGoal;

		if (cursorId != null) {
			cursorGoal = goalRepository.findByIdAndStatus(cursorId, Status.ACTIVE)
				.orElseThrow(() -> new CustomException(ErrorCode.GOAL_NOT_FOUND));
		} else {
			cursorGoal = null;
		}

		if (sort == null) {
			sort = Sort.RECENT;
		}

		List<ShareGoalResponseDto> shareGoalList = goalRepository.findByAuthIdAndCursorAndFilters(
				foundUser.getId(), cursorGoal, cursorId, size, category, share, sort, period
			)
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();

		return ShareGoalCursorResponseDto.from(shareGoalList);
	}

	@Transactional
	@Override
	public List<ShareGoalResponseDto> getShareGoalsToUser(Long authId, Long userId) {

		User foundAuthUser = validUser(authId);

		User foundUser = validUser(userId);
		return goalRepository.findByAuthIdAndUserId(foundAuthUser.getId(), foundUser.getId())
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();
	}

	@Transactional
	@Override
	public ShareGoalCursorResponseDto searchShareGoals(Long authId, Long cursorId, int size, String keyword) {

		User foundUser = validUser(authId);

		List<ShareGoalResponseDto> responseDtoList = goalRepository.findByAuthIdAndCursorAndTitleContaining(
				foundUser.getId(), cursorId, size, keyword)
			.stream()
			.map(ShareGoalResponseDto::from)
			.toList();

		return ShareGoalCursorResponseDto.from(responseDtoList);
	}

	@Transactional
	@Override
	public void plusSearchKeyword(Long authId, ShareGoalSearchRequestDto shareGoalSearchRequestDto) {

		User foundUser = validUser(authId);

		String category = shareGoalSearchRequestDto.getCategory().toString();

		String keyword = shareGoalSearchRequestDto.getKeyword();

		String title = shareGoalSearchRequestDto.getTitle();

		String accurateKeyword = shareGoalSearchRequestDto.getTags()
			.stream()
			.filter(title::contains)
			.filter(tag -> similarity(tag, keyword))
			.findFirst()
			.orElse(null);

		if (accurateKeyword != null) {
			String key = "search:keywords";

			String value = String.format("%s:%s", category, accurateKeyword);

			redisTemplate.opsForZSet().incrementScore(key, value, 1);
		}
	}

	@Override
	public List<ShareGoalSearchResponseDto> searchTop10Keyword(Long authId) {

		User foundUser = validUser(authId);

		String key = "search:keywords";

		Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
			.reverseRangeWithScores(key, 0, 9);

		if (results == null || results.isEmpty()) {
			return List.of();
		}

		return results.stream()
			.map(a -> ShareGoalSearchResponseDto.of(a.getValue(), a.getScore()))
			.toList();
	}

	@Override
	public List<String> searchAutocomplete(Long authId, String prefix) {

		User foundUser = validUser(authId);

		String key = "search:keywords";

		Set<String> allValue = redisTemplate.opsForZSet().reverseRange(key, 0, -1);

		if (allValue == null) {
			return List.of();
		}

		return allValue.stream()

			// value가 category:tag 이런 식으로 저장되어 있어서 :로 분리, 어떠한 이유로 인해 잘못된 데이터가 들어갈 시에 방어코드도 추가
			.map(value -> {
				try {
					return value.split(":")[1];
				} catch (Exception e) {
					log.warn("잘못된 형식의 데이터 입니다. value: {}", value);
					return null;
				}
			})
			.filter(Objects::nonNull)
			.filter(value -> value.startsWith(prefix))
			.toList();
	}

	private User validUser(Long userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	private boolean similarity(String tag, String keyword) {
		if (tag.equals(keyword)) {
			return true;
		}
		if (tag.startsWith(keyword)) {
			return true;
		}

		if (tag.contains(keyword)) {
			return true;
		}

		// 검색하고 점수를 올리는 것이 한 묶음인데 예외처리를 던지게 되면 사용자 입장에서 불편할 것 같아서 내부적으로만 로그
		log.info("태그와 키워드가 일치하지 않습니다");
		return false;
	}
}
