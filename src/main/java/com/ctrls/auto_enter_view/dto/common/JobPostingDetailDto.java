package com.ctrls.auto_enter_view.dto.common;

import com.ctrls.auto_enter_view.entity.JobPostingEntity;
import com.ctrls.auto_enter_view.enums.TechStack;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class JobPostingDetailDto {

  @Getter
  @AllArgsConstructor
  @Builder
  public static class Response {

    private String jobPostingKey;
    private String companyKey;
    private String title;
    private String jobCategory;
    private Integer career;
    private String workLocation;
    private String education;
    private String employmentType;
    private Long salary;
    private String workTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String jobPostingContent;

    private List<String> techStack;
    private List<String> step;
    private String image;

    public static Response from(JobPostingEntity entity, List<TechStack> techStack,
        List<String> step, String imageUrl) {

      return Response.builder()
          .jobPostingKey(entity.getJobPostingKey())
          .companyKey(entity.getCompanyKey())
          .title(entity.getTitle())
          .jobCategory(entity.getJobCategory())
          .career(entity.getCareer())
          .workLocation(entity.getWorkLocation())
          .education(entity.getEducation())
          .employmentType(entity.getEmploymentType())
          .salary(entity.getSalary())
          .workTime(entity.getWorkTime())
          .startDate(entity.getStartDate())
          .endDate(entity.getEndDate())
          .jobPostingContent(entity.getJobPostingContent())
          .techStack(techStack.stream().map(TechStack::getValue).collect(Collectors.toList()))
          .step(step)
          .image(imageUrl)
          .build();
    }
  }
}