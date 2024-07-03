package com.ctrls.auto_enter_view.service;

import com.ctrls.auto_enter_view.dto.company.ChangePasswordDto;
import com.ctrls.auto_enter_view.dto.company.SignUpDto;
import com.ctrls.auto_enter_view.entity.CompanyEntity;
import com.ctrls.auto_enter_view.enums.ResponseMessage;
import com.ctrls.auto_enter_view.repository.CompanyRepository;
import com.ctrls.auto_enter_view.util.KeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final KeyGenerator keyGenerator;
  private final PasswordEncoder passwordEncoder;

  public SignUpDto.Response signUp(SignUpDto.Request form) {

    // 이메일 중복 확인

    // 이메일 인증번호 확인

    // 키 생성
    String companyKey = KeyGenerator.generateKey();

    CompanyEntity companyEntity = form.toEntity(companyKey,
        passwordEncoder.encode(form.getPassword()));

    CompanyEntity saved = companyRepository.save(companyEntity);

    return SignUpDto.Response.builder()
        .companyKey(saved.getCompanyKey())
        .email(saved.getEmail())
        .name(saved.getCompanyName())
        .message(ResponseMessage.SIGNUP.getMessage())
        .build();
  }

  public void changePassword(String companyKey, ChangePasswordDto.Request form) {

    CompanyEntity companyEntity = companyRepository.findByCompanyKey(companyKey)
        .orElseThrow(RuntimeException::new);

    // 입력한 비밀번호가 맞는 지 확인
    if (!passwordEncoder.matches(form.getOldPassword(), companyEntity.getPassword())) {
      throw new RuntimeException();
    }

    companyEntity.setPassword(passwordEncoder.encode(form.getNewPassword()));

    companyRepository.save(companyEntity);
  }
}