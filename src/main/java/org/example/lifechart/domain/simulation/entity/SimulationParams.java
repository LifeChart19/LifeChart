package org.example.lifechart.domain.simulation.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationParams {


    private String title;

    //기준일 사용자 설정 가능
    private Date BaseDate;

    //최초 자산
    private Long initialAsset;

    //수입
    private Long monthlyIncome;

    //지출
    private Long monthlyExpense;


}
