package org.example.lifechart.validation.validator;

import java.util.List;

import org.example.lifechart.validation.annotation.ValidTags;
import org.example.lifechart.validation.support.TagValidatable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TagsValidator implements ConstraintValidator<ValidTags, TagValidatable> {
	@Override
	public boolean isValid(TagValidatable dto, ConstraintValidatorContext constraintValidatorContext) {
		// 강남 집 샀다. / 강남 집을 드디어 샀다. / 강남집 샀다.
		String title = dto.getTitle();
		// [집], [바나나]면 false;
		List<String> tags = dto.getTags();

		for (String tag : tags) {

			// 태그는 제목에 키워드를 포함해야 함
			if (!title.contains(tag)) {
				return false;
			}

			// 특수문자, 이모지, 공백 x
			if (!tag.matches("^[가-힣a-zA-Z0-9]+$")) {
				return false;
			}
		}
		return true;
	}
}
