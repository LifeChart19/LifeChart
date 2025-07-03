package org.example.lifechart.domain.goal.repository;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;

public interface ApartmentPriceCacheRepository {
	Optional<ApartmentPriceDto> findOldest(String region, String subregion);
	Optional<Pair<ApartmentPriceDto, ApartmentPriceDto>> findStartAndEnd(String region, String subregion); // start-end 쌍 조회
	Optional<ApartmentPriceDto> findLatest(String region, String subregion);
	void save(ApartmentPriceDto dto);
	void saveWithAlias(ApartmentPriceDto dto, boolean isStart);
}