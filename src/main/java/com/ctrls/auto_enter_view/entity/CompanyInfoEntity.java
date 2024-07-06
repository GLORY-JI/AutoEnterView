package com.ctrls.auto_enter_view.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "company_info")
public class CompanyInfoEntity extends BaseEntity {

  @Id
  private String companyInfoKey;

  @Column(nullable = false)
  private String company_key;

  @Column(nullable = false)
  private int employees;

  @Column(nullable = false)
  private LocalDateTime companyAge;

  @Column(nullable = false)
  private String companyUrl;

  @Column(nullable = false)
  private String boss;

  @Column(nullable = false)
  private String address;
}