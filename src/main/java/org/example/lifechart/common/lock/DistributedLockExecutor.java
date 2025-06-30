package org.example.lifechart.common.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockExecutor {

	private final RedissonClient redissonClient;

	// Supplier<T>은 Runnable과 같이 어떤 작업을 실행해주는 것은 같으나 반환값을 반환까지 해줌
	public <T> T executeWithLock(String key, Supplier<T> action, long wait, long lease, TimeUnit unit) {
		RLock lock = redissonClient.getLock(key); // 키 기반으로 락 객체 생성 및 가져오기
		boolean locked = false;

		try {
			locked = lock.tryLock(wait, lease, unit); // 최대 대기 시간, 유지 시간, 시간 단위
			if (!locked) {
				log.warn("락 획득 실패: key={}", key);
				throw new CustomException(ErrorCode.LOCK_FAILED);
			}
			return action.get(); // 메서드 반환값 반환
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // 현재 쓰레드가 중단 됐었는 지 기록
			log.error("락 처리 중단: key={}, error={}", key, e.getMessage());
			throw new CustomException(ErrorCode.LOCK_INTERRUPTION);
		} finally {
			if (locked && lock.isHeldByCurrentThread()) { // 현재 쓰레드 락 보유 중인지
				lock.unlock();
			}
		}
	}

	public void executeWithLock(String key, Runnable action, long wait, long lease, TimeUnit unit) {
		executeWithLock(key, ()-> {
			action.run();
			return null;
		}, wait, lease, unit); // 바로 위의 메서드 재사용
	}
}
