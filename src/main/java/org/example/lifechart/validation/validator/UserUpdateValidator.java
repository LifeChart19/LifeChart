package org.example.lifechart.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.springframework.util.StringUtils;

public class UserUpdateValidator implements ConstraintValidator<org.example.lifechart.validation.annotation.ValidUserUpdate, UserUpdateRequest> {

    @Override
    public boolean isValid(UserUpdateRequest request, ConstraintValidatorContext context) {
        return StringUtils.hasText(request.getNickname())
                || StringUtils.hasText(request.getGender())
                || StringUtils.hasText(request.getJob())
                || StringUtils.hasText(request.getPhoneNumber());
    }
}
