package com.ctrls.auto_enter_view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseMessage {
  CHANGE_PASSWORD("비밀번호 변경 완료."),
  SIGNUP("회원가입을 축하드립니다."),
  SUCCESS_CREATE_COMPANY_INFO("회사 정보 생성이 완료되었습니다."),
  SUCCESS_DELETE_COMPANY_INFO("회사 정보 삭제가 완료되었습니다."),
  SUCCESS_EMAIL_VERIFY("이메일 인증 성공."),
  SUCCESS_SEND_CODE("인증 코드 전송 성공."),
  SUCCESS_TEMPORARY_PASSWORD_SEND("임시 비밀번호 전송 성공."),
  SUCCESS_UPDATE_COMPANY_INFO("회사 정보 수정이 완료되었습니다."),
  USABLE_EMAIL("사용 가능한 이메일입니다."),
  WITHDRAW("회원탈퇴 완료."),
  SUCCESS_JOB_POSTING_APPLY("지원이 완료되었습니다."),
  SUCCESS_EDIT_JOB_POSTING("채용 공고 수정 완료."),
  SUCCESS_DELETE_JOB_POSTING("채용 공고 삭제 완료."),
  SUCCESS_PERSONAL_INTERVIEW_SCHEDULE("개인 면접 일정 생성 완료."),
  SUCCESS_INTERVIEW_SCHEDULE("면접 일정 생성 완료.");


  private final String message;
}