package com.ctrls.auto_enter_view.dto.candidate;

import com.ctrls.auto_enter_view.entity.CandidateEntity;
import com.ctrls.auto_enter_view.enums.UserRole;
import com.ctrls.auto_enter_view.util.KeyGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignUpDto {

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor

  public static class Request {

    @NotBlank(message = "이름은 필수 입력값 입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값 입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "인증번호는 필수 입력값 입니다.")
    private String verificationCode;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "전화번호는 필수 입력 값 입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    public CandidateEntity toEntity(Request request, String password) {

      return CandidateEntity.builder()
          .candidateKey(KeyGenerator.generateKey())
          .name(request.getName())
          .email(request.getEmail())
          .password(password)
          .phoneNumber(request.getPhoneNumber())
          .role(UserRole.ROLE_CANDIDATE)
          .build();
    }
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String candidateKey;

    private String name;

    private String email;
  }
}