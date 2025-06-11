package org.example.lifechart.domain.goal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.enums.RetirementMonthlyExpense;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class StandardValueService {

	// 기대수명 데이터: 연도 → (성별 → 기대수명)
	private Map<String, Map<String, Double>> lifespanMap;

	//Json 로드
	@PostConstruct
	public void init() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		InputStream is = getClass().getClassLoader().getResourceAsStream("data/lifespan_data.json");
		if (is == null) {
			throw new CustomException(ErrorCode.GOAL_INIT_DATA_MISSING);
		}
		TypeReference<Map<String, Map<String, Double>>> typeRef = new TypeReference<>() {};
		this.lifespanMap = objectMapper.readValue(is, typeRef);
	}

	// 기대 수명 반환 메서드
	public Long getExpectedLifespan(String gender, int currentYear) {
		Map<String, Double> byGender = lifespanMap.get(String.valueOf(currentYear));
		if (byGender == null) {
			throw new CustomException(ErrorCode.GOAL_LIFESPAN_DATA_NOT_EXIST);
		}

		// gender가 없을 경우 "total" 값 사용
		Double lifespan = byGender.getOrDefault(gender.toLowerCase(), byGender.get("total"));

		if (lifespan == null) {
			throw new CustomException(ErrorCode.GOAL_LIFESPAN_DATA_NOT_EXIST);
		}

		return Math.round(lifespan); // 소수점 반올림
	}

	// 평균 지출 메서드도 이곳에 함께 구현
	public Long getAverageMonthlyExpense(RetirementType retirementType) {
		return RetirementMonthlyExpense.valueOf(retirementType.name()).getValue();
	}
}