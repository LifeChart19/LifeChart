package org.example.lifechart.domain.goal.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.lifechart.domain.goal.enums.RetirementType;

@Getter
@Builder
public class GoalRetirementInfoResponse implements GoalDetailInfoResponse{

    private Long expectedLifespan;
    private Long MonthlyExpense;
    private RetirementType retirementType;
}
