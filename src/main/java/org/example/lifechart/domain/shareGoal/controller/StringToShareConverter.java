package org.example.lifechart.domain.shareGoal.controller;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.enums.Share;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToShareConverter implements Converter<String, Share> {

	@Override
	public Share convert(String source) {
		try {
			return Share.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.CONVERT_BAD_REQUEST);
		}
	}
}
