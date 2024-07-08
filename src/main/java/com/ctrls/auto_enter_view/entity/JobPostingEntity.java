package com.ctrls.auto_enter_view.entity;

import com.ctrls.auto_enter_view.dto.jobposting.JobPostingDto.Request;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_posting")
public class JobPostingEntity extends BaseEntity {

  @Id
  private String jobPostingKey;

  @Column(nullable = false)
  private String companyKey;

  @Column(nullable = false)
  private String title;

  private String jobCategory;

  private Integer career;

  @Column(nullable = false)
  private String workLocation;

  private String education;

  @Column(nullable = false)
  private String employmentType;

  @Column(nullable = false)
  private Long salary;

  @Column(nullable = false)
  private String workTime;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  private String jobPostingContent;

  public void updateEntity(Request request) {
    this.title = request.getTitle();
    this.jobCategory = request.getJobCategory();
    this.career = request.getCareer();
    this.workLocation = request.getWorkLocation();
    this.education = request.getEducation();
    this.employmentType = request.getEmploymentType();
    this.salary = request.getSalary();
    this.workTime = request.getWorkTime();
    this.startDate = request.getStartDate();
    this.endDate = request.getEndDate();
    this.jobPostingContent = request.getJobPostingContent();
  }

}