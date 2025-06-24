package org.example.lifechart.domain.goal.service;

import java.io.IOException;

public interface ApartmentPriceService {
	Long getAveragePrice(String region, String subregion, Long area);
	Long getFuturePredictedPrice(String region, String subregion, Long area, int yearsLater);
}
