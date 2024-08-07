package com.ctrls.auto_enter_view.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "candidate_list")
public class CandidateListEntity extends BaseEntity {

  @Id
  private String candidateListKey;

  @Column(nullable = false)
  private Long jobPostingStepId;

  @Column(nullable = false)
  private String jobPostingKey;

  @Column(nullable = false)
  private String candidateKey;

  @Column(nullable = false)
  private String candidateName;

  public void updateJobPostingStepId(Long jobPostingStepId) {
    this.jobPostingStepId = jobPostingStepId;
  }
}