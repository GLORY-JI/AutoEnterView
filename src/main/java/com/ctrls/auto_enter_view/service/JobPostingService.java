package com.ctrls.auto_enter_view.service;

import static com.ctrls.auto_enter_view.enums.ErrorCode.COMPANY_NOT_FOUND;
import static com.ctrls.auto_enter_view.enums.ErrorCode.JOB_POSTING_HAS_CANDIDATES;
import static com.ctrls.auto_enter_view.enums.ErrorCode.JOB_POSTING_NOT_FOUND;
import static com.ctrls.auto_enter_view.enums.ErrorCode.JOB_POSTING_STEP_NOT_FOUND;
import static com.ctrls.auto_enter_view.enums.ErrorCode.NO_AUTHORITY;
import static com.ctrls.auto_enter_view.enums.ErrorCode.USER_NOT_FOUND;

import com.ctrls.auto_enter_view.component.MailComponent;
import com.ctrls.auto_enter_view.dto.common.JobPostingDetailDto;
import com.ctrls.auto_enter_view.dto.common.MainJobPostingDto;
import com.ctrls.auto_enter_view.dto.common.MainJobPostingDto.JobPostingMainInfo;
import com.ctrls.auto_enter_view.dto.jobPosting.JobPostingDto.Request;
import com.ctrls.auto_enter_view.dto.jobPosting.JobPostingInfoDto;
import com.ctrls.auto_enter_view.entity.ApplicantEntity;
import com.ctrls.auto_enter_view.entity.AppliedJobPostingEntity;
import com.ctrls.auto_enter_view.entity.CandidateListEntity;
import com.ctrls.auto_enter_view.entity.CompanyEntity;
import com.ctrls.auto_enter_view.entity.JobPostingEntity;
import com.ctrls.auto_enter_view.entity.JobPostingStepEntity;
import com.ctrls.auto_enter_view.enums.ErrorCode;
import com.ctrls.auto_enter_view.enums.TechStack;
import com.ctrls.auto_enter_view.exception.CustomException;
import com.ctrls.auto_enter_view.repository.ApplicantRepository;
import com.ctrls.auto_enter_view.repository.AppliedJobPostingRepository;
import com.ctrls.auto_enter_view.repository.CandidateListRepository;
import com.ctrls.auto_enter_view.repository.CandidateRepository;
import com.ctrls.auto_enter_view.repository.CompanyRepository;
import com.ctrls.auto_enter_view.repository.JobPostingRepository;
import com.ctrls.auto_enter_view.repository.JobPostingStepRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobPostingService {

  private final JobPostingRepository jobPostingRepository;
  private final ApplicantRepository applicantRepository;
  private final CompanyRepository companyRepository;
  private final CandidateListRepository candidateListRepository;
  private final CandidateRepository candidateRepository;
  private final JobPostingTechStackService jobPostingTechStackService;
  private final JobPostingStepRepository jobPostingStepRepository;
  private final AppliedJobPostingRepository appliedJobPostingRepository;
  private final JobPostingStepService jobPostingStepService;
  private final JobPostingImageService jobPostingImageService;
  private final FilteringService filteringService;
  private final MailComponent mailComponent;

  /**
   * 채용 공고 생성하기
   *
   * @param companyKey
   * @param request
   * @return
   */
  public JobPostingEntity createJobPosting(UserDetails userDetails, String companyKey, Request request) {

    // 회사 정보 조회
    CompanyEntity company = companyRepository.findByCompanyKey(companyKey)
        .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

    // 현재 회사의 권한 체크
    if (!company.getEmail().equals(userDetails.getUsername())) {
      throw new CustomException(NO_AUTHORITY);
    }

    JobPostingEntity entity = Request.toEntity(companyKey, request);

    // 스케줄링 코드
    filteringService.scheduleResumeScoringJob(entity.getJobPostingKey(), entity.getEndDate());

    return jobPostingRepository.save(entity);
  }

  /**
   * 채용 공고 수정하기
   *
   * @param jobPostingKey
   * @param request
   */
  public void editJobPosting(String jobPostingKey, Request request) {

    JobPostingEntity jobPostingEntity = jobPostingRepository.findByJobPostingKey(jobPostingKey)
        .orElseThrow(() -> new CustomException(JOB_POSTING_NOT_FOUND));

    // 지원자 목록 조회
    List<CandidateListEntity> candidateListEntityList = candidateListRepository.findAllByJobPostingKeyAndJobPostingStepId(
        jobPostingKey, getJobPostingStepEntity(jobPostingKey).getId());

    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    CompanyEntity companyEntity = companyRepository.findByEmail(principal.getUsername())
        .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

    if (!companyEntity.getCompanyKey().equals(jobPostingEntity.getCompanyKey())) {
      throw new CustomException(NO_AUTHORITY);
    }

    // 이전에 스케줄된 작업 취소
    filteringService.unscheduleResumeScoringJob(jobPostingKey);

    // 채용 공고 수정
    jobPostingEntity.updateEntity(request);

    // 새로운 스케줄 설정
    filteringService.scheduleResumeScoringJob(jobPostingKey, request.getEndDate());

    // 지원자 목록을 순회하며 이메일 보내기
    notifyCandidates(candidateListEntityList, jobPostingEntity);
  }

  /**
   * 채용 공고 삭제하기
   *
   * @param jobPostingKey
   */
  public void deleteJobPosting(String jobPostingKey) {

    if (verifyExistsByJobPostingKey(jobPostingKey)) {
      throw new CustomException(JOB_POSTING_HAS_CANDIDATES);
    }

    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    CompanyEntity companyEntity = companyRepository.findByEmail(principal.getUsername())
        .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

    JobPostingEntity jobPostingEntity = jobPostingRepository.findByJobPostingKey(jobPostingKey)
        .orElseThrow(() ->
            new CustomException(JOB_POSTING_NOT_FOUND));

    if (!companyEntity.getCompanyKey().equals(jobPostingEntity.getCompanyKey())) {
      throw new CustomException(NO_AUTHORITY);
    }

    jobPostingRepository.deleteByJobPostingKey(jobPostingKey);
  }

  /**
   * 회사 본인이 등록한 채용공고 목록 조회
   *
   * @param companyKey
   * @return
   */
  public List<JobPostingInfoDto> getJobPostingsByCompanyKey(
      String companyKey) {

    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    CompanyEntity company = findCompanyByPrincipal(principal);

    verifyCompanyOwnership(company, companyKey);

    List<JobPostingEntity> jobPostingEntityList = jobPostingRepository.findAllByCompanyKey(
        companyKey);

    return jobPostingEntityList.stream()
        .map(JobPostingInfoDto::fromEntity)
        .collect(Collectors.toList());
  }


  /**
   * Main 화면 채용 공고 조회
   *
   * @param page
   * @param size
   * @return
   */
  public MainJobPostingDto.Response getAllJobPosting(int page, int size) {

    Pageable pageable = PageRequest.of(page - 1, size);
    LocalDate currentDate = LocalDate.now();

    Page<JobPostingEntity> jobPostingPage = jobPostingRepository.findByEndDateGreaterThanEqual(currentDate, pageable);
    List<MainJobPostingDto.JobPostingMainInfo> jobPostingMainInfoList = new ArrayList<>();

    for (JobPostingEntity entity : jobPostingPage.getContent()) {
      JobPostingMainInfo jobPostingMainInfo = createJobPostingMainInfo(entity);
      jobPostingMainInfoList.add(jobPostingMainInfo);
    }

    log.info("총 {}개의 채용 공고 조회 완료", jobPostingMainInfoList.size());
    return MainJobPostingDto.Response.builder()
        .jobPostingsList(jobPostingMainInfoList)
        .totalPages(jobPostingPage.getTotalPages())
        .totalElements(jobPostingPage.getTotalElements())
        .build();
  }

  /**
   * 채용 공고 상세 보기
   *
   * @param jobPostingKey
   * @return
   */
  public JobPostingDetailDto.Response getJobPostingDetail(String jobPostingKey) {

    LocalDate currentDate = LocalDate.now();

    JobPostingEntity jobPosting = jobPostingRepository.findByJobPostingKey(jobPostingKey)
        .orElseThrow(() -> new CustomException(JOB_POSTING_NOT_FOUND));

    // 마감일 지났는지 체크
    if (!jobPostingRepository.existsByJobPostingKeyAndEndDateGreaterThanEqual(jobPostingKey, currentDate)) {
      throw new CustomException(ErrorCode.JOB_POSTING_EXPIRED);
    }

    List<TechStack> techStack = getTechStack(jobPosting.getJobPostingKey());
    List<String> step = getStep(jobPosting.getJobPostingKey());
    String imageUrl = getImageUrl(jobPosting.getJobPostingKey());

    return JobPostingDetailDto.Response.from(jobPosting, techStack, step, imageUrl);
  }

  /**
   * 채용 공고 지원하기
   *
   * @param jobPostingKey
   * @param candidateKey
   */
  @Transactional
  public void applyJobPosting(String jobPostingKey, String candidateKey) {

    JobPostingEntity jobPostingEntity = jobPostingRepository.findByJobPostingKey(jobPostingKey)
        .orElseThrow(() -> new CustomException(
            JOB_POSTING_NOT_FOUND));

    // 채용 지원 중복 체크
    boolean isApplied = applicantRepository.existsByCandidateKeyAndJobPostingKey(candidateKey,
        jobPostingKey);
    if (isApplied) {
      throw new CustomException(ErrorCode.ALREADY_APPLIED);
    }

    // 응시자 추가하기
    ApplicantEntity applicantEntity = ApplicantEntity.builder()
        .jobPostingKey(jobPostingKey)
        .candidateKey(candidateKey)
        .score(0)
        .build();

    applicantRepository.save(applicantEntity);

    log.info("지원 완료 - jobPostingKey: {}, candidateKey: {}", jobPostingKey, candidateKey);

    // 지원한 공고 목록 추가하기
    AppliedJobPostingEntity appliedJobPostingEntity = AppliedJobPostingEntity.builder()
        .jobPostingKey(jobPostingKey)
        .candidateKey(candidateKey)
        .startDate(LocalDate.now())
        .stepName("지원 완료")
        .title(jobPostingEntity.getTitle())
        .build();

    appliedJobPostingRepository.save(appliedJobPostingEntity);
    log.info("AppliedJobPosting 추가 완료");

  }

  /**
   * 이미지 URL 가져오기
   *
   * @param jobPostingKey
   * @return
   */
  private String getImageUrl(String jobPostingKey) {

    String imageUrl = jobPostingImageService.getImageUrl(jobPostingKey);
    log.info("이미지 URL 조회 완료 : {}", imageUrl);
    return imageUrl;
  }

  /**
   * 채용 공고 단계 중 맨 처음 단계 가져오기
   * @param jobPostingKey
   * @return
   */
  private JobPostingStepEntity getJobPostingStepEntity(String jobPostingKey) {

    return jobPostingStepRepository.findFirstByJobPostingKeyOrderByIdAsc(
        jobPostingKey).orElseThrow(() -> new CustomException(JOB_POSTING_STEP_NOT_FOUND));
  }

  /**
   * 사용자 인증 정보로 회사 entity 찾기
   *
   * @param principal
   * @return
   */
  private CompanyEntity findCompanyByPrincipal(User principal) {

    return companyRepository.findByEmail(principal.getUsername())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
  }

  /**
   * 회사 본인인지 확인
   *
   * @param company
   * @param companyKey
   */
  private void verifyCompanyOwnership(CompanyEntity company, String companyKey) {

    if (!company.getCompanyKey().equals(companyKey)) {
      throw new CustomException(NO_AUTHORITY);
    }
  }

  /**
   * 채용 공고에 지원한 지원자가 존재하는지 확인 : 채용 공고의 첫번째 단계에 해당하는 지원자 목록 확인
   *
   * @param jobPostingKey
   * @return
   */
  private boolean verifyExistsByJobPostingKey(String jobPostingKey) {

    Long firstStep = getJobPostingStepEntity(jobPostingKey).getId();

    return candidateListRepository.existsByJobPostingKeyAndJobPostingStepId(
        jobPostingKey, firstStep);
  }

  /**
   * 전체 체용 공고 List 들어갈 정보
   *
   * @param entity
   * @return
   */
  private JobPostingMainInfo createJobPostingMainInfo(JobPostingEntity entity) {

    String companyName = getCompanyName(entity.getCompanyKey());
    List<TechStack> techStack = getTechStack(entity.getJobPostingKey());

    return JobPostingMainInfo.from(entity, companyName, techStack);
  }

  /**
   * 회사 이름 가져오기
   *
   * @param companyKey
   * @return
   */
  private String getCompanyName(String companyKey) {

    CompanyEntity companyEntity = companyRepository.findByCompanyKey(companyKey)
        .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));

    String companyName = companyEntity.getCompanyName();
    log.info("회사명 조회 완료 : {}", companyName);
    return companyName;
  }

  /**
   * 기술 스택 가져오기
   *
   * @param jobPostingKey
   * @return
   */
  private List<TechStack> getTechStack(String jobPostingKey) {

    List<TechStack> techStack = jobPostingTechStackService.getTechStackByJobPostingKey(
        jobPostingKey);
    log.info("기술 스택 조회 완료 : {}", techStack);
    return techStack;
  }

  /**
   * 채용 일정 가져오기
   *
   * @param jobPostingKey
   * @return
   */
  private List<String> getStep(String jobPostingKey) {

    List<String> step = jobPostingStepService.getStepByJobPostingKey(jobPostingKey);
    log.info("채용 단계 조회 완료 : {}", step);
    return step;
  }

  /**
   * 지원자에게 이메일 보내기 메서드
   *
   * @param candidates
   * @param jobPostingEntity
   */
  private void notifyCandidates(List<CandidateListEntity> candidates,
      JobPostingEntity jobPostingEntity) {

    for (CandidateListEntity candidate : candidates) {
      String to = candidateRepository.findByCandidateKey(candidate.getCandidateKey())
          .orElseThrow(() -> new CustomException(USER_NOT_FOUND)).getEmail();
      String subject = "채용 공고 수정 알림 : " + jobPostingEntity.getTitle();
      String text =
          "지원해주신 <strong>[" + jobPostingEntity.getTitle()
              + "]</strong>의 공고 내용이 수정되었습니다. 확인 부탁드립니다.<br><br>"
              + "<a href=\"http://localhost:8080/common/job-postings/"
              + jobPostingEntity.getJobPostingKey() + "\">수정된 채용 공고 확인하기</a>";
      mailComponent.sendHtmlMail(to, subject, text, true);
    }
  }
}