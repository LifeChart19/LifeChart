package org.example.lifechart.domain.goal.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;

@Getter
@Builder
public class GoalInfoResponse {

    private String title;
    private Category category;
    private Long targetAmount;
    private Float progressRate;
    private Status status;
    private Share share;
    private GoalDetailInfoResponse detail;
}
