package org.example.lifechart.domain.user.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import org.example.lifechart.domain.user.entity.User;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String gender;
    private String job;
    private String phoneNumber;
    private LocalDate birthDate;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getName();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.job = user.getJob();
        this.phoneNumber = user.getPhoneNumber();
        this.birthDate = user.getBirthDate();
    }
}
