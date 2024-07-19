package com.ctrls.auto_enter_view.repository;

import com.ctrls.auto_enter_view.entity.AppliedJobPostingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedJobPostingRepository extends JpaRepository<AppliedJobPostingEntity, Long> {

  Page<AppliedJobPostingEntity> findAllByCandidateKey(String candidateKey, Pageable pageable);
}