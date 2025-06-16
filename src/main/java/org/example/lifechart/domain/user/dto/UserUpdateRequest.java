package org.example.lifechart.domain.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String nickname;
    private String gender;
    private String job;
    private String phoneNumber;
}
