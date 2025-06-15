package org.example.lifechart.domain.goal.dto.mapper;

import org.example.lifechart.domain.goal.dto.response.GoalDetailInfoResponse;
import org.example.lifechart.domain.goal.dto.response.GoalInfoResponse;
import org.example.lifechart.domain.goal.entity.Goal;

public class GoalMapper {
    public static GoalInfoResponse toGoalInfoResponse(Goal goal, GoalDetailInfoResponse detail, float progressRate) {
        return GoalInfoResponse.builder()
                .title(goal.getTitle())
                .category(goal.getCategory())
                .targetAmount(goal.getTargetAmount())
                .status(goal.getStatus())
                .share(goal.getShare())
                .detail(detail)
                .build();
    }
}
