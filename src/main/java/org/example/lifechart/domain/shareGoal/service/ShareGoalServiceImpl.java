package org.example.lifechart.domain.shareGoal.service;

import java.util.List;
import java.util.Set;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.shareGoal.dto.reqeust.ShareGoalSearchRequestDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalCursorResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalResponseDto;
import org.example.lifechart.domain.shareGoal.dto.response.ShareGoalSearchResponseDto;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShareGoalServiceImpl implements ShareGoalService {
	private final GoalRepository goalRepository;
	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	@Override
	public ShareGoalCursorResponseDto getShareGoals(
		Long authId, Long cursorId, int size, Category category, Share share
	) {

		User foundUser = validUser(authId);

		List<ShareGoalResponseDto> shareGoalList = goalRepository.findByAuthIdAndCursorAndFilters(
				foundUser.getId(), cursorId, size, category, share
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

		String accurateKeyword = shareGoalSearchRequestDto.getTags()
			.stream()
			.filter(tag -> tag.equals(keyword))
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

	private User validUser(Long userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId)
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
