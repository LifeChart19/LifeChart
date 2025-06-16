package org.example.lifechart.domain.user.dto;

import lombok.Getter;
import org.example.lifechart.validation.annotation.ValidUserUpdate;

@ValidUserUpdate
@Getter
public class UserUpdateRequest {
    private String nickname;
    private String gender;
    private String job;
    private String phoneNumber;
}
