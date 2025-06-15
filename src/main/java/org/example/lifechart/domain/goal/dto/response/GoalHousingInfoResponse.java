package org.example.lifechart.domain.goal.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.lifechart.domain.goal.enums.HousingType;

@Getter
@Builder
public class GoalHousingInfoResponse implements GoalDetailInfoResponse{

    private String region;
    private String subregion;
    private Long area;
    private HousingType housingType;
}
