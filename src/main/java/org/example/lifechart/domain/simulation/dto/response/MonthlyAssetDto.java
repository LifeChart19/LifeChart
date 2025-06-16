package org.example.lifechart.domain.simulation.dto.response;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.YearMonth;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MonthlyAssetDto {
    private YearMonth month;
    private Long asset;

}