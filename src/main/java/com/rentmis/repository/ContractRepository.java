package com.rentmis.repository;

import com.rentmis.model.entity.Contract;
import com.rentmis.model.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByContractNumber(String contractNumber);

    Optional<Contract> findByContractHash(String hash);

    Optional<Contract> findByBlockchainTxHash(String txHash);

    Page<Contract> findByTenantId(Long tenantId, Pageable pageable);

    Page<Contract> findByLandlordId(Long landlordId, Pageable pageable);

    List<Contract> findByUnitIdAndStatus(Long unitId, ContractStatus status);

    Optional<Contract> findByUnitIdAndStatusIn(Long unitId, List<ContractStatus> statuses);

    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE' AND c.endDate <= :date")
    List<Contract> findExpiringContracts(@Param("date") LocalDate date);

    @Query("SELECT c FROM Contract c WHERE " +
           "(:tenantId IS NULL OR c.tenant.id = :tenantId) AND " +
           "(:landlordId IS NULL OR c.landlord.id = :landlordId) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Contract> filterContracts(@Param("tenantId") Long tenantId,
                                   @Param("landlordId") Long landlordId,
                                   @Param("status") ContractStatus status,
                                   Pageable pageable);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.status = 'ACTIVE'")
    Long countActiveContracts();

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.status = 'ACTIVE' AND c.landlord.id = :landlordId")
    Long countActiveContractsByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT c.status, COUNT(c) FROM Contract c GROUP BY c.status")
    List<Object[]> countByStatus();

    @Query("SELECT c.status, COUNT(c) FROM Contract c WHERE c.landlord.id = :landlordId GROUP BY c.status")
    List<Object[]> countByStatusForLandlord(@Param("landlordId") Long landlordId);
}
