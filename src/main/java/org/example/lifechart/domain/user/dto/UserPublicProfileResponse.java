package org.example.lifechart.domain.user.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import org.example.lifechart.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class UserPublicProfileResponse {
    private String nickname;
    private String email;
    private String job;
    private String gender;

    public UserPublicProfileResponse(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.job = user.getJob();
        this.gender = user.getGender();
    }
}
