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

	@Scheduled(cron = "0 0 2 * * *")
	public void top10Reset() {
		String key = "search:keywords";
		redisTemplate.delete(key);
		log.info("{} 인기 검색어가 삭제되었습니다", key);
	}
}
