package org.example.lifechart.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SignupRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.") // 일단은 4자리만 추후에 특수문자등 추가
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;


    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;


    // 선택 입력
    private BigDecimal salary;
    private String gender;
    private String job;
    private String phoneNumber;
}


