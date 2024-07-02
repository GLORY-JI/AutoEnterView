package com.ctrls.auto_enter_view.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Table(name = "resume")
public class ResumeEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String resumeKey;

  private String candidateKey;

  private String title;

  private String jobWant;

  private String name;

  private String gender;

  private LocalDate birthDate;

  private String email;

  private String phoneNumber;

  private String address;

  private String scholarship;

  private String portfolio;
}