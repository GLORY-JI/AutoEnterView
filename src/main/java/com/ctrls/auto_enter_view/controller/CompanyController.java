package com.ctrls.auto_enter_view.controller;

import com.ctrls.auto_enter_view.dto.company.ChangePasswordDto;
import com.ctrls.auto_enter_view.dto.company.SignUpDto;
import com.ctrls.auto_enter_view.enums.ResponseMessage;
import com.ctrls.auto_enter_view.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CompanyController {

  private final CompanyService companyService;

  @PostMapping("/companies/signup")
  public ResponseEntity<?> signUp(
      @RequestBody SignUpDto.Request form) {

    SignUpDto.Response response = companyService.signUp(form);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/companies/{companyKey}/password")
  public ResponseEntity<?> changePassword(
      @PathVariable String companyKey,
      @RequestBody ChangePasswordDto.Request form) {

    companyService.changePassword(companyKey, form);

    return ResponseEntity.ok(ResponseMessage.CHANGE_PASSWORD.getMessage());
  }
}