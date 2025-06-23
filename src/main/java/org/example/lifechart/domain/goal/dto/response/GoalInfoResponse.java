package org.example.lifechart.domain.goal.dto.response;

import java.util.List;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalInfoResponse {

    private String title;
    private Category category;
    private Long targetAmount;
    private Status status;
    private Share share;
    private GoalDetailInfoResponse detail;
    private List<String> tags;

    public static GoalInfoResponse from(Goal goal, GoalDetailInfoResponse detail) {
        return GoalInfoResponse.builder()
            .title(goal.getTitle())
            .category(goal.getCategory())
            .targetAmount(goal.getTargetAmount())
            .status(goal.getStatus())
            .share(goal.getShare())
            .detail(detail)
            .tags(goal.getTags())
            .build();

    }
}
