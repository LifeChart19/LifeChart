package org.example.lifechart.domain.shareGoal.scheduler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class Top10Scheduler {
	private final RedisTemplate<String, String> redisTemplate;

	// 매일 새벽 2시
	@Scheduled(cron = "0 0 2 * * *")
	public void top10Reset() {
		String key = "search:keywords";
		Boolean isDelete = redisTemplate.delete(key);
		if (isDelete) {
			log.info("{}를 가진 인기 검색어가 삭제되었습니다", key);
		} else {
			log.info("{}가 존재하지 않습니다", key);
		}
	}
}
