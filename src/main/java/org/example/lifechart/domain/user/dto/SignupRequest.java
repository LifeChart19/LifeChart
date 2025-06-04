package org.example.lifechart.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.") // 일단은 4자리만 추후에 특수문자등 추가
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    private int age;
}
