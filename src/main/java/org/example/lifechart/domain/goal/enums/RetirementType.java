package org.example.lifechart.domain.goal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RetirementType {
    COUPLE("부부"), // 부부
    SOLO("개인"); // 개인

    private final String description;
}