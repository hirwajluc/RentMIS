package com.rentmis.repository;

import com.rentmis.model.entity.User;
import com.rentmis.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByPasswordResetToken(String token);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true " +
           "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchByRole(@Param("role") Role role, @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    Long countByRoleAndActive(@Param("role") Role role);

    @Query("SELECT COUNT(DISTINCT u) FROM User u " +
           "JOIN Unit un ON un.currentTenant.id = u.id " +
           "WHERE u.role = 'TENANT' AND u.isActive = true AND un.property.landlord.id = :landlordId")
    Long countTenantsByLandlordId(@Param("landlordId") Long landlordId);

    // Returns ALL active tenants (including flagged ones) for contract creation
    @Query("SELECT DISTINCT u FROM User u WHERE u.role = 'TENANT' AND u.isActive = true ORDER BY u.firstName, u.lastName")
    java.util.List<User> findAllActiveTenants();

    // IDs of tenants with a VERIFIED report
    @Query("SELECT DISTINCT tr.tenant.id FROM TenantReport tr WHERE tr.status = 'VERIFIED'")
    java.util.Set<Long> findTenantIdsWithVerifiedReport();

    // IDs of tenants with overdue payments (past period still PENDING or FAILED)
    @Query("SELECT DISTINCT p.tenant.id FROM Payment p " +
           "WHERE p.status IN ('PENDING', 'FAILED') " +
           "AND (p.paymentPeriodYear * 12 + p.paymentPeriodMonth) < :currentPeriod")
    java.util.Set<Long> findTenantIdsWithOverduePayments(@Param("currentPeriod") int currentPeriod);
}
