package org.example.lifechart.domain.shareGoal.controller;

import org.example.lifechart.domain.goal.enums.Share;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToShareConverter implements Converter<String, Share> {

	@Override
	public Share convert(String source) {
		return Share.valueOf(source.toUpperCase());
	}
}
