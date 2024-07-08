package com.ctrls.auto_enter_view.service;

import com.ctrls.auto_enter_view.dto.jobposting.JobPostingDto.Request;
import com.ctrls.auto_enter_view.entity.JobPostingEntity;
import com.ctrls.auto_enter_view.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobPostingService {

  private final JobPostingRepository jobPostingRepository;

  public JobPostingEntity createJobPosting(String companyKey, Request request) {

    JobPostingEntity entity = Request.toEntity(companyKey, request);

    return jobPostingRepository.save(entity);
  }

  public void editJobPosting(String jobPostingKey, Request request) {

    JobPostingEntity entity = jobPostingRepository.findByJobPostingKey(jobPostingKey);
    entity.updateEntity(request);

  }


  public void deleteJobPosting(String jobPostingKey) {
    jobPostingRepository.deleteByJobPostingKey(jobPostingKey);
  }
}
