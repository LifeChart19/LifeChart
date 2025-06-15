package org.example.lifechart.domain.goal.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

import org.example.lifechart.domain.goal.entity.GoalRetirement;
import org.example.lifechart.domain.goal.enums.RetirementType;
import org.example.lifechart.domain.goal.helper.GoalDateHelper;

@Getter
@Builder
public class GoalRetirementInfoResponse implements GoalDetailInfoResponse{

    private LocalDate expectedDeathDate;
    private Long MonthlyExpense;
    private RetirementType retirementType;

    public static GoalRetirementInfoResponse from(GoalRetirement goalRetirement) {
        return GoalRetirementInfoResponse.builder()
            .expectedDeathDate(goalRetirement.getExpectedDeathDate())
            .MonthlyExpense(goalRetirement.getMonthlyExpense())
            .retirementType(goalRetirement.getRetirementType())
            .build();
    }
}
