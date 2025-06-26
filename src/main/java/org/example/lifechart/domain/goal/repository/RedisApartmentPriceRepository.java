package org.example.lifechart.domain.goal.repository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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

	private String generateKey(String region, String subregion, String period) {
		return "apt-price::" + region + "::" + subregion + "::" + period;
	}

	@Override
	public Optional<ApartmentPriceDto> find(String region, String subregion) {
		String key = generateKey(region, subregion, null);
		String value = redisTemplate.opsForValue().get(key);

		if (value == null)
			return Optional.empty();

		try {
			ApartmentPriceDto dto = objectMapper.readValue(value, ApartmentPriceDto.class);
			return Optional.of(dto);
		} catch (JsonProcessingException e) {
			return Optional.empty(); // or Log error
		}
	}

	@Override
	public Optional<Pair<ApartmentPriceDto, ApartmentPriceDto>> findStartAndEnd(String region, String subregion, int years) {

		// 1. 데이터 중 가장 최신 period 구하기
		String latestPeriod = findLatestPeriod(region, subregion); // 예: "202504"
		YearMonth end = YearMonth.parse(latestPeriod, DateTimeFormatter.ofPattern("yyyyMM"));
		YearMonth start = end.minusYears(years);

		String startKey = generateKey(region, subregion, start.format(DateTimeFormatter.ofPattern("yyyyMM")));
		String endKey = generateKey(region, subregion, latestPeriod);

		ApartmentPriceDto startDto = null;
		ApartmentPriceDto endDto = null;

		try {
			String startJson = redisTemplate.opsForValue().get(startKey);
			if (startJson != null) {
				startDto = objectMapper.readValue(startJson, ApartmentPriceDto.class);
			}

			String endJson = redisTemplate.opsForValue().get(endKey);
			if (endJson != null) {
				endDto = objectMapper.readValue(endJson, ApartmentPriceDto.class);
			}

			if (startDto != null && endDto != null) {
				return Optional.of(Pair.of(startDto, endDto));
			}
		} catch (Exception e) {
			log.warn("Redis price fetch failed: {}", e.getMessage());
		}

		return Optional.empty();
	}

	@Override
	public void save(ApartmentPriceDto dto) {
		String key = generateKey(dto.getRegion(), dto.getSubregion(), dto.getPeriod());
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, TTL);
		} catch (JsonProcessingException e) {
			// Log error
		}
	}

	private String findLatestPeriod(String region, String subregion) {

		String prefix = "apt-price::" + region + "::" + subregion + "::";
		ScanOptions options = ScanOptions.scanOptions()
			.match(prefix + "*")
			.count(100)
			.build();

		Set<String> periods = new HashSet<>();

		try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
			.getConnection()
			.scan(options)) {

			while (cursor.hasNext()) {
				String key = new String(cursor.next(), StandardCharsets.UTF_8); // byte[] -> String
				String[] parts = key.split("::");
				if (parts.length == 4) {
					periods.add(parts[3]);
				}
			}

		} catch (Exception e) {
			log.error("Redis SCAN 실패 : {}", e.getMessage(), e);
			throw new CustomException(ErrorCode.EXTERNAL_API_FAILURE);
		}

		log.debug("Latest periods found for {}-{}: {}", region, subregion, periods);

		// 최신 period 반환
		return periods.stream()
			.max(Comparator.naturalOrder()) // 문자열 정렬
			.orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));
	}
}
