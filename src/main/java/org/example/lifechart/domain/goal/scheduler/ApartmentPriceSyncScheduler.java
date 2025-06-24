package org.example.lifechart.domain.goal.scheduler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;
import org.example.lifechart.domain.goal.enums.RegionCode;
import org.example.lifechart.domain.goal.repository.ApartmentPriceCacheRepository;
import org.example.lifechart.domain.goal.service.OpenApiApartmentPriceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApartmentPriceSyncScheduler {

	private final OpenApiApartmentPriceService apiService;
	private final ApartmentPriceCacheRepository redisRepository;

	@Scheduled(cron = "0 0 0 1 * *")
	public void sync() {
		for (RegionCode code : RegionCode.getValidCodes()) {
			String region = code.getRegion();
			String subregion = code.getSubregion();

			try {
				ApartmentPriceDto dto = apiService.fetchLatest(region, subregion);
				if (dto != null) {
					redisRepository.save(dto);
				}
			} catch (Exception e) {
				log.warn("Failed to fetch/save apartment price for region: {}, subregion: {}", region, subregion, e);
			}
		}
	}
}
