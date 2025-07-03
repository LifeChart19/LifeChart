package org.example.lifechart.domain.shareGoal.controller;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.enums.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCategoryConverter implements Converter<String, Category> {

	@Override
	public Category convert(String source) {
		try {
			return Category.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.CONVERT_BAD_REQUEST);
		}
	}
}
