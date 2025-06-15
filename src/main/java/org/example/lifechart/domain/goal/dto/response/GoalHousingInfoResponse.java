package org.example.lifechart.domain.goal.dto.response;

import lombok.Builder;
import lombok.Getter;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.example.lifechart.domain.goal.enums.HousingType;

@Getter
@Builder
public class GoalHousingInfoResponse implements GoalDetailInfoResponse{

    private String region;
    private String subregion;
    private Long area;
    private HousingType housingType;

    public static GoalHousingInfoResponse from(GoalHousing goalHousing) {
        return GoalHousingInfoResponse.builder()
            .region(goalHousing.getRegion())
            .subregion(goalHousing.getSubregion())
            .area(goalHousing.getArea())
            .housingType(goalHousing.getHousingType())
            .build();
    }
}
