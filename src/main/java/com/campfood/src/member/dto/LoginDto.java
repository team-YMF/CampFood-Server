package com.campfood.src.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LoginDto {

    @NotBlank(message = "로그인id는 필수입력값입니다.")
    private String loginId;
    @NotBlank(message = "비밀번호는 필수입력값입니다.")
    private String password;
}
