package org.example.lifechart.domain.simulation.service.simulation;

import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.springframework.stereotype.Component;

@Component
public class SimulationValidator {

    public void validateSimulationParams(BaseCreateSimulationRequestDto dto) {

        if (dto.getMonthlyExpense() > dto.getMonthlyIncome()) {
            throw new IllegalArgumentException("월 지출이 월 수입보다 클 수 없습니다.");
        }

//        if (dto.getMonthlyIncome() > totalTargetAmount) {
//            throw new IllegalArgumentException("월 수입이 목표 금액 총합보다 클 수 없습니다."); // 예시 메시지 → 비즈니스 정책에 맞게 작성
//        }

//        if (dto.getBaseDate().toLocalDate().isBefore(LocalDate.now())) {
//            throw new IllegalArgumentException("기준일은 이전 날짜로 설정할 수 없습니다.");
//        }


        //이후 추가될 수도

    }

}