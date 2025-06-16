package org.example.lifechart.domain.simulation.dto.response;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

//embeddable db에 저장되길 원한다면 필요.
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MonthlyAchievement {

    private YearMonth month; // 연/월 정보
    private float achievementRate; // 달성률
}