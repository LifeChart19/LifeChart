package org.example.lifechart.domain.shareGoal.controller;

import org.example.lifechart.domain.shareGoal.enums.Sort;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSortConverter implements Converter<String, Sort> {
	@Override
	public Sort convert(String source) {
		return Sort.valueOf(source.toUpperCase());
	}
}
