package org.example.lifechart.domain.comment.service;

import java.util.concurrent.TimeUnit;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.lock.DistributedLockExecutor;
import org.example.lifechart.domain.comment.dto.request.CommentRequestDto;
import org.example.lifechart.domain.comment.dto.response.CommentResponseDto;
import org.example.lifechart.domain.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockCommentService {
	private final DistributedLockExecutor lockExecutor;
	private final CommentService commentService;
	private final CommentRepository commentRepository;
	private final String defaultKey = "lock:goal:comment:";
	private static final long LOCK_WAIT_TIME = 30L;
	private static final long LOCK_LEASE_TIME_CREATE = 5L;
	private static final long LOCK_LEASE_TIME_DELETE = 3L;


	public CommentResponseDto createComment(Long authId, Long goalId, CommentRequestDto commentRequestDto) {
		String key = defaultKey + "create:" + goalId;
		return lockExecutor.executeWithLock(key, () -> {
			long start = System.nanoTime();
			CommentResponseDto responseDto = commentService.createComment(authId, goalId, commentRequestDto);
			long end = System.nanoTime();
			long elapsedMs = (end - start) / 1_000_000;
			log.info("댓글 생성 시간: {}ms", elapsedMs); // 동시성 제어할 때 댓글 생성의 평균 작업시간을 구하기 위함
			return responseDto;
			}, LOCK_WAIT_TIME,  LOCK_LEASE_TIME_CREATE, TimeUnit.SECONDS);
	}

	public void deleteComment(Long authId, Long commentId) {
		Long goalId = commentRepository.findGoalIdByCommentId(commentId).orElseThrow(() ->
			new CustomException(ErrorCode.COMMENT_NOT_FOUND)); // goalId를 키로 넣어야 하는데 받는 로직이 없어서 가져옴
		String key = defaultKey + "delete:" + goalId;
		lockExecutor.executeWithLock(key, () -> {
			long start = System.nanoTime();
			commentService.deleteComment(authId, commentId);
			long end = System.nanoTime();
			long elapsedMs = (end - start) / 1_000_000;
			log.info("댓글 삭제 시간: {}ms", elapsedMs);
			}, LOCK_WAIT_TIME, LOCK_LEASE_TIME_DELETE, TimeUnit.SECONDS);
	}
}
