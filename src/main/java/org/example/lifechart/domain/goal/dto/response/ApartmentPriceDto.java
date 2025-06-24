package org.example.lifechart.domain.goal.dto.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApartmentPriceDto implements Serializable {

	private String region;
	private String subregion;
	private String period; // 기준연월
	private double price; // 가격
	private String unit; // 가격 단위
}
