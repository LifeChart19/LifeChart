package org.example.lifechart.domain.shareGoal.controller;

import java.time.Period;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPeriodConverter implements Converter<String, Period> {
	@Override
	public Period convert(String source) {
		// 연, 월, 일주일, 일,
		if (source.equals("연")) {
			return Period.ofYears(1);
		}
		if (source.equals("월")) {
			return Period.ofMonths(1);
		}
		if (source.equals("일주일")) {
			return Period.ofDays(7);
		}
		if (source.equals("일")) {
			return Period.ofDays(1);
		}
		throw new CustomException(ErrorCode.SHARE_GOAL_PERIOD_BAD_REQUEST);
	}
}
