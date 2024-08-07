package com.ctrls.auto_enter_view.service;

import com.ctrls.auto_enter_view.component.S3ImageUpload;
import com.ctrls.auto_enter_view.dto.jobPosting.JobPostingDto;
import com.ctrls.auto_enter_view.entity.JobPostingImageEntity;
import com.ctrls.auto_enter_view.repository.JobPostingImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingImageService {

  private final JobPostingImageRepository jobPostingImageRepository;
  private final S3ImageUpload s3ImageUpload;

  /**
   * 이미지 파일 업로드
   *
   * @param image         이미지 파일
   * @param jobPostingKey 채용공고 KEY
   * @return 채용공고 DTO
   */
  public JobPostingDto.Response uploadImage(MultipartFile image, String jobPostingKey) {
    log.info("채용공고 이미지 파일 업로드");

    JobPostingImageEntity jobPostingImage = jobPostingImageRepository.findByJobPostingKey(
            jobPostingKey)
        .orElseGet(() -> JobPostingImageEntity.builder()
            .jobPostingKey(jobPostingKey)
            .build());

    if (jobPostingImage.getCompanyImageUrl() != null) {
      log.info("기존 이미지가 있다면 삭제");
      s3ImageUpload.deleteImage(jobPostingImage.getCompanyImageUrl());
    }

    String imageUrl = s3ImageUpload.uploadImage(image, "job-posting-images");
    jobPostingImage.updateCompanyImageUrl(imageUrl);
    jobPostingImageRepository.save(jobPostingImage);

    return new JobPostingDto.Response(jobPostingKey, imageUrl);
  }

  // 이미지 파일 조회 -> Response 반환
  public JobPostingDto.Response getJobPostingImage(String jobPostingKey) {
    log.info("채용공고 이미지 파일 조회");

    return jobPostingImageRepository.findByJobPostingKey(jobPostingKey)
        .map(image -> new JobPostingDto.Response(jobPostingKey, image.getCompanyImageUrl()))
        .orElse(new JobPostingDto.Response(jobPostingKey, null));
  }

  // 이미지 파일 삭제
  public void deleteImage(String jobPostingKey) {
    log.info("채용공고 이미지 파일 삭제");

    jobPostingImageRepository.findByJobPostingKey(jobPostingKey)
        .ifPresent(image -> {
          String imageUrl = image.getCompanyImageUrl();
          s3ImageUpload.deleteImage(imageUrl);
          jobPostingImageRepository.delete(image);
        });
  }
}