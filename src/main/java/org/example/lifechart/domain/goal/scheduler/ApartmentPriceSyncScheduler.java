package org.example.lifechart.domain.goal.scheduler;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
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
		try {
			Map<RegionCode, Pair<ApartmentPriceDto, ApartmentPriceDto>> allData = apiService.fetchDurationAll(120);

			for (Map.Entry<RegionCode, Pair<ApartmentPriceDto, ApartmentPriceDto>> entry : allData.entrySet()) {
				ApartmentPriceDto start = entry.getValue().getLeft();
				ApartmentPriceDto end = entry.getValue().getRight();

				redisRepository.save(start); // key: apt-price:서울:동남권:201507
				redisRepository.save(end); // key: apt-price:서울:동남권:202507

				redisRepository.saveWithAlias(start, true); // key: apt-price:서울:동남권:start
				redisRepository.saveWithAlias(end, false); // key: apt-price:서울:동남권:end
			}
			log.info("아파트 가격 데이터 동기화 성공. 지역 수: {}", allData.size());
		} catch (Exception e) {
			log.error("아파트 가격 데이터 동기화 실패", e);
		}
	}
}