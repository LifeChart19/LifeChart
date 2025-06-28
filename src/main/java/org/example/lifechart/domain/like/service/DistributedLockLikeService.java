package org.example.lifechart.domain.like.service;

import java.util.concurrent.TimeUnit;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.lock.DistributedLockExecutor;
import org.example.lifechart.domain.like.dto.response.LikeResponseDto;
import org.example.lifechart.domain.like.repository.LikeRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockLikeService {
	private final DistributedLockExecutor lockExecutor;
	private final LikeService likeService;
	private final LikeRepository likeRepository;
	private final String defaultKey = "lock:goal:like:";

	public LikeResponseDto plusLike(Long authId, Long goalId) {
		String key = defaultKey + "plus:" + goalId;
		return lockExecutor.executeWithLock(key, () -> {
			long start = System.nanoTime();
			LikeResponseDto responseDto = likeService.plusLike(authId, goalId);
			long end = System.nanoTime();
			long elapsedMs = (end - start) / 1_000_000;
			log.info("좋아요 생성 시간: {}ms", elapsedMs);
			return responseDto;
			}, 50, 5, TimeUnit.SECONDS);
	}

	public void deleteLike(Long authId, Long likeId) {
		Long goalId = likeRepository.findGoalIdByLikeId(likeId).orElseThrow(() ->
			new CustomException(ErrorCode.LIKE_NOT_FOUND));
		String key = defaultKey + "delete:" + goalId;
		lockExecutor.executeWithLock(key, () -> {
			long start = System.nanoTime();
			likeService.deleteLike(authId, likeId);
			long end = System.nanoTime();
			long elapsedMs = (end - start) / 1_000_000;
			log.info("좋아요 삭제 시간: {}ms", elapsedMs);
			}, 30, 5, TimeUnit.SECONDS);
	}
}
