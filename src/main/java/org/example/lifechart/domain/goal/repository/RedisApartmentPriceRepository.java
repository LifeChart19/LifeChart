package org.example.lifechart.domain.goal.repository;

import java.time.Duration;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisApartmentPriceRepository implements ApartmentPriceCacheRepository {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final Duration TTL = Duration.ofDays(30); // 30일 캐시 유지

	@Override
	public Optional<ApartmentPriceDto> findOldest(String region, String subregion) {
		String key = generateKey(region, subregion, "start");
		return fetchFromRedis(key);
	}

	@Override
	public Optional<Pair<ApartmentPriceDto, ApartmentPriceDto>> findStartAndEnd(String region, String subregion) {

		String startKey = generateKey(region, subregion, "start");
		String endKey = generateKey(region, subregion, "end");

		try {
			ApartmentPriceDto startDto = fetchFromRedis(startKey).orElse(null);
			ApartmentPriceDto endDto = fetchFromRedis(endKey).orElse(null);

			if (startDto != null && endDto != null) {
				return Optional.of(Pair.of(startDto, endDto));
			}
		} catch (Exception e) {
			log.warn("Redis 가격 조회 실패: {}", e.getMessage());
		}

		return Optional.empty();
	}

	@Override
	public Optional<ApartmentPriceDto> findLatest(String region, String subregion) {
		String key = generateKey(region, subregion, "end"); // 최신 가격을 "end"키에 저장 중
		return fetchFromRedis(key);
	}

	@Override
	public void save(ApartmentPriceDto dto) {
		String key = generateKey(dto.getRegion(), dto.getSubregion(), dto.getPeriod());
		saveToRedis(key, dto);
	}

	@Override
	public void saveWithAlias(ApartmentPriceDto dto, boolean isStart) {
		String alias = isStart ? "start" : "end";
		String key = generateKey(dto.getRegion(), dto.getSubregion(), alias);
		saveToRedis(key, dto);
	}

	private String generateKey(String region, String subregion, String periodOrAlias) {
		return String.format("apt-price:%s:%s:%s", region, subregion ,periodOrAlias);
	}

	private void saveToRedis(String key, ApartmentPriceDto dto) {
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, TTL);
		} catch (JsonProcessingException e) {
			log.warn("Redis 저장 실패. {} : {}", key, e.getMessage());
		}
	}

	private Optional<ApartmentPriceDto> fetchFromRedis(String key) {
		try {
			String json = redisTemplate.opsForValue().get(key);
			if (json != null) {
				return Optional.of(objectMapper.readValue(json, ApartmentPriceDto.class));
			}
		} catch (Exception e) {
			log.warn("Redis 읽기 실패. {}: {}", key, e.getMessage());
		}
		return Optional.empty();
	}
}
