package org.example.lifechart.domain.goal.dto.response;

import org.example.lifechart.domain.goal.entity.GoalEtc;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalEtcInfoResponse implements GoalDetailInfoResponse{

    private String theme;
    private Long expectedPrice;

    public static GoalEtcInfoResponse from(GoalEtc goalEtc) {
        return GoalEtcInfoResponse.builder()
            .theme(goalEtc.getTheme())
            .expectedPrice(goalEtc.getExpectedPrice())
            .build();
    }
}
