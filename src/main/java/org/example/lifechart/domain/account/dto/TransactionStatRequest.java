package org.example.lifechart.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.YearMonth;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatRequest {
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth startYM;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth endYM;
}

