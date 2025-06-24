package org.example.lifechart.domain.goal.repository;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;

public interface ApartmentPriceCacheRepository {
	Optional<ApartmentPriceDto> find(String region, String subregion);
	Optional<Pair<ApartmentPriceDto, ApartmentPriceDto>> findStartAndEnd(String region, String subregion, int years);
	void save(ApartmentPriceDto dto);
}
