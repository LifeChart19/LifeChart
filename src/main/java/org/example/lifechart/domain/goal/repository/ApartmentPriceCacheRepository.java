package org.example.lifechart.domain.goal.repository;

import java.util.Optional;

import org.example.lifechart.domain.goal.dto.response.ApartmentPriceDto;

public interface ApartmentPriceCacheRepository {
	Optional<ApartmentPriceDto> find(String region, String subregion);
	void save(ApartmentPriceDto dto);
}
