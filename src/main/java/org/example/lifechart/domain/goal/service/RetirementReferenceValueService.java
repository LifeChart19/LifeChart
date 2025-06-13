package org.example.lifechart.domain.goal.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.dto.response.GoalRetirementEstimateResponse;
import org.example.lifechart.domain.goal.enums.RetirementMonthlyExpense;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

@Service
public class RetirementReferenceValueService {

	// 기대수명 데이터: 연도 → (성별 → 기대수명)
	private Map<String, Map<String, Double>> lifespanMap;
	private final UserRepository userRepository;

	// 운영 환경용 생성자
	@Autowired
	public RetirementReferenceValueService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// 테스트 전용 생성자
	public RetirementReferenceValueService(Map<String, Map<String, Double>> lifespanMap, UserRepository userRepository) {
		this.lifespanMap = lifespanMap;
		this.userRepository = userRepository;
	}

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

	public GoalRetirementEstimateResponse getReferenceValues(Long userId, int currentYear) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


		Long expectedLifespan = getExpectedLifespan(user.getGender(), currentYear);
		RetirementType retirementType = RetirementType.COUPLE;
		Long MonthlyExpense = getAverageMonthlyExpense(retirementType);

		return new GoalRetirementEstimateResponse(expectedLifespan, MonthlyExpense, retirementType);
	}

	// 기대 수명 반환 메서드
	private Long getExpectedLifespan(String gender, int currentYear) {
		Map<String, Double> byGender = lifespanMap.get(String.valueOf(currentYear));
		if (byGender == null) {
			// fallback : 현재 년도 데이터가 없을 경우, 가장 최신 연도를 사용해 조회
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
	private Long getAverageMonthlyExpense(RetirementType retirementType) {
		return RetirementMonthlyExpense.valueOf(retirementType.name()).getValue();
	}
}