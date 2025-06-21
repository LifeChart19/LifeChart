package org.example.lifechart.domain.goal.repository;

import java.time.Duration;
import java.util.Optional;

import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisApartmentPriceRepository implements ApartmentPriceCacheRepository {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final Duration TTL = Duration.ofDays(1); // 1일 캐시 유지

	private String generateKey(String region, String subregion) {
		return "apt-price::" + region + "::" + subregion;
	}

	@Override
	public Optional<ApartmentPriceDto> find(String region, String subregion) {
		String key = generateKey(region, subregion);
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
	public void save(ApartmentPriceDto dto) {
		String key = generateKey(dto.getRegion(), dto.getSubregion());
		try {
			String json = objectMapper.writeValueAsString(dto);
			redisTemplate.opsForValue().set(key, json, TTL);
		} catch (JsonProcessingException e) {
			// Log error
		}
	}
}
