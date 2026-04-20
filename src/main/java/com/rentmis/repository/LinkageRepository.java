package com.rentmis.repository;

import com.rentmis.model.entity.PropertyLinkage;
import com.rentmis.model.enums.LinkageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkageRepository extends JpaRepository<PropertyLinkage, Long> {

    /** All linkages created by a specific agent (paginated). */
    Page<PropertyLinkage> findByAgentIdOrderByCreatedAtDesc(Long agentId, Pageable pageable);

    /** Active (non-rejected, non-expired) linkages for a unit — used to detect existing leads. */
    @Query("SELECT l FROM PropertyLinkage l WHERE l.unit.id = :unitId " +
           "AND l.status IN ('PENDING', 'ACCEPTED')")
    List<PropertyLinkage> findActiveByUnitId(@Param("unitId") Long unitId);

    /** Active linkages for a property+unit — used when contract is signed to trigger commission. */
    @Query("SELECT l FROM PropertyLinkage l WHERE l.property.id = :propertyId " +
           "AND (:unitId IS NULL OR l.unit.id = :unitId) " +
           "AND l.status IN ('PENDING', 'ACCEPTED')")
    List<PropertyLinkage> findActiveForPropertyUnit(@Param("propertyId") Long propertyId,
                                                    @Param("unitId") Long unitId);

    /** Count linkages by agent and status (for dashboard stats). */
    long countByAgentIdAndStatus(Long agentId, LinkageStatus status);

    /** Count all linkages by agent. */
    long countByAgentId(Long agentId);
}
