package org.example.lifechart.domain.goal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentPriceServiceImpl implements ApartmentPriceService {

	private Map<String, Map<String, Double>> apartmentPriceMap;

	@PostConstruct
	public void init() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream is = getClass().getClassLoader().getResourceAsStream("apartment_price.json");
		TypeReference<Map<String, Map<String, Double>>> typeRef = new TypeReference<>() {};
		this.apartmentPriceMap = objectMapper.readValue(is, typeRef);
	}

	@Override
	public Long getAveragePrice(String region, String subregion, Long area) {
		Double pricePerMeter = apartmentPriceMap.getOrDefault(region, Map.of())
			.getOrDefault(subregion, 0.0);
		return Math.round(pricePerMeter * area);
	}
}
