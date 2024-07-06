package com.ctrls.auto_enter_view.repository;

import com.ctrls.auto_enter_view.entity.ResumeCertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeCertificateRepository extends JpaRepository<ResumeCertificateEntity, Long> {

}