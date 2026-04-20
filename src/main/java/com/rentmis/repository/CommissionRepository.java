package com.rentmis.repository;

import com.rentmis.model.entity.Commission;
import com.rentmis.model.enums.CommissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {

    Page<Commission> findByAgentIdOrderByCreatedAtDesc(Long agentId, Pageable pageable);

    Optional<Commission> findByLinkageId(Long linkageId);

    long countByAgentIdAndStatus(Long agentId, CommissionStatus status);

    long countByAgentId(Long agentId);
}
