package org.example.lifechart.domain.simulation.dto.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateSimulationRequestDto {

    //제목
    private String title;

    //기준일 사용자 설정 가능
    private LocalDateTime BaseDate;

    //최초 자산
    private Long initialAsset;

    //수입
    private Long monthlyIncome;

    //지출
    private Long monthlyExpense;

    //시뮬레이션을 2개이상 돌리는 경우
    List<Long> goalIds = new ArrayList<>();

}
