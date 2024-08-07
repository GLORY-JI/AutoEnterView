package com.ctrls.auto_enter_view.controller;

import com.ctrls.auto_enter_view.dto.jobPosting.JobPostingEveryInfoDto;
import com.ctrls.auto_enter_view.dto.jobPostingStep.EditJobPostingStepDto;
import com.ctrls.auto_enter_view.enums.ResponseMessage;
import com.ctrls.auto_enter_view.service.JobPostingStepService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/job-postings/{jobPostingKey}")
@RequiredArgsConstructor
@RestController
public class JobPostingStepController {

  private final JobPostingStepService jobPostingStepService;

  /**
   * 전체 채용 단계의 지원자 리스트 조회하기 : 채용단계 ID - 지원자 key, 지원자 이름, 이력서 key, 기술 스택 리스트, 면접 일시
   *
   * @param jobPostingKey 지원자 PK
   * @return List<JobPostingEveryInfoDto.Response>
   */
  @GetMapping("/candidates-list")
  public ResponseEntity<List<JobPostingEveryInfoDto>> getCandidatesList(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable String jobPostingKey) {
    return ResponseEntity.ok(
        jobPostingStepService.getCandidatesListByStepId(userDetails, jobPostingKey));
  }

  /**
   * 채용 공고에 지원한 지원자 단계 이동 시키기
   *
   * @param request EditJobPostingStepDto.Request
   * @param jobPostingKey 채용 공고 PK
   * @param userDetails 로그인 된 사용자 정보
   * @return ResponseMessage
   */
  @PutMapping("/edit-step")
  public ResponseEntity<String> editStepId(
      @RequestBody @Validated EditJobPostingStepDto request,
      @PathVariable String jobPostingKey,
      @AuthenticationPrincipal UserDetails userDetails) {
    jobPostingStepService.editStepId(request.getCurrentStepId(), request.getCandidateKeys(),
        jobPostingKey, userDetails);
    return ResponseEntity.ok(ResponseMessage.SUCCESS_STEP_MOVEMENT.getMessage());
  }
}