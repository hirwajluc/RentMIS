package com.rentmis.repository;

import com.rentmis.model.entity.Payment;
import com.rentmis.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByGlspayTransactionId(String transactionId);

    List<Payment> findAllByGlspayTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = com.rentmis.model.enums.PaymentStatus.PENDING_CONFIRMATION " +
           "AND prop.landlord.id = :landlordId ORDER BY p.createdAt DESC")
    List<Payment> findPendingConfirmationByLandlord(@Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(p) FROM Payment p LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = com.rentmis.model.enums.PaymentStatus.PENDING_CONFIRMATION " +
           "AND prop.landlord.id = :landlordId")
    Long countPendingConfirmationByLandlord(@Param("landlordId") Long landlordId);

    Page<Payment> findByTenantId(Long tenantId, Pageable pageable);

    Page<Payment> findByUnitId(Long unitId, Pageable pageable);

    List<Payment> findByTenantIdAndPaymentPeriodMonthAndPaymentPeriodYear(
            Long tenantId, Integer month, Integer year);

    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN p.unit u LEFT JOIN u.property prop WHERE " +
           "(:tenantId   IS NULL OR p.tenant.id          = :tenantId)   AND " +
           "(:unitId     IS NULL OR p.unit.id             = :unitId)     AND " +
           "(:landlordId IS NULL OR prop.landlord.id      = :landlordId) AND " +
           "(:status     IS NULL OR p.status              = :status)     AND " +
           "(:from       IS NULL OR p.createdAt           >= :from)      AND " +
           "(:to         IS NULL OR p.createdAt           <= :to)")
    Page<Payment> filterPayments(@Param("tenantId")   Long tenantId,
                                  @Param("unitId")     Long unitId,
                                  @Param("landlordId") Long landlordId,
                                  @Param("status")     PaymentStatus status,
                                  @Param("from")       LocalDateTime from,
                                  @Param("to")         LocalDateTime to,
                                  Pageable pageable);

    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND p.paidAt BETWEEN :from AND :to")
    BigDecimal sumCompletedPayments(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND MONTH(p.paidAt) = :month AND YEAR(p.paidAt) = :year")
    Long countCompletedByMonthYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND MONTH(p.paidAt) = :month AND YEAR(p.paidAt) = :year")
    BigDecimal sumCompletedByMonthYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.dueDate < CURRENT_DATE")
    List<Payment> findOverduePayments();

    // Monthly revenue trend - returns List<Object[]> {month, year, total}
    @Query(value = "SELECT MONTH(paid_at) as month, YEAR(paid_at) as year, SUM(total_amount) as total " +
                   "FROM payments WHERE status = 'COMPLETED' AND paid_at >= :from " +
                   "GROUP BY YEAR(paid_at), MONTH(paid_at) ORDER BY year, month",
           nativeQuery = true)
    List<Object[]> monthlyRevenueTrend(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED'")
    Long countCompletedPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING'")
    Long countPendingPayments();

    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal totalRevenue();

    // ── Landlord-scoped versions ─────────────────────────────────────────────

    @Query("SELECT SUM(p.totalAmount) FROM Payment p " +
           "LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = 'COMPLETED' AND prop.landlord.id = :landlordId")
    BigDecimal totalRevenueByLandlord(@Param("landlordId") Long landlordId);

    @Query("SELECT SUM(p.totalAmount) FROM Payment p " +
           "LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = 'COMPLETED' AND prop.landlord.id = :landlordId " +
           "AND MONTH(p.paidAt) = :month AND YEAR(p.paidAt) = :year")
    BigDecimal sumCompletedByMonthYearAndLandlord(@Param("month") int month,
                                                   @Param("year") int year,
                                                   @Param("landlordId") Long landlordId);

    @Query("SELECT SUM(p.totalAmount) FROM Payment p " +
           "LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = 'COMPLETED' AND prop.landlord.id = :landlordId " +
           "AND p.paidAt BETWEEN :from AND :to")
    BigDecimal sumCompletedPaymentsByLandlord(@Param("from") LocalDateTime from,
                                               @Param("to") LocalDateTime to,
                                               @Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(p) FROM Payment p " +
           "LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = 'COMPLETED' AND prop.landlord.id = :landlordId")
    Long countCompletedPaymentsByLandlord(@Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(p) FROM Payment p " +
           "LEFT JOIN p.unit u LEFT JOIN u.property prop " +
           "WHERE p.status = 'PENDING' AND prop.landlord.id = :landlordId")
    Long countPendingPaymentsByLandlord(@Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED'")
    Long countFailedPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'FAILED' " +
           "AND p.unit.property.landlord.id = :landlordId")
    Long countFailedPaymentsByLandlord(@Param("landlordId") Long landlordId);

    @Query("SELECT AVG(p.totalAmount) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal avgCompletedAmount();

    @Query("SELECT AVG(p.totalAmount) FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND p.unit.property.landlord.id = :landlordId")
    BigDecimal avgCompletedAmountByLandlord(@Param("landlordId") Long landlordId);

    // Top payers: tenant id, name, count, total
    @Query("SELECT p.tenant.id, p.tenant.firstName, p.tenant.lastName, COUNT(p), SUM(p.totalAmount) " +
           "FROM Payment p WHERE p.status = 'COMPLETED' " +
           "GROUP BY p.tenant.id, p.tenant.firstName, p.tenant.lastName " +
           "ORDER BY SUM(p.totalAmount) DESC")
    List<Object[]> topPayers(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p.tenant.id, p.tenant.firstName, p.tenant.lastName, COUNT(p), SUM(p.totalAmount) " +
           "FROM Payment p WHERE p.status = 'COMPLETED' AND p.unit.property.landlord.id = :landlordId " +
           "GROUP BY p.tenant.id, p.tenant.firstName, p.tenant.lastName " +
           "ORDER BY SUM(p.totalAmount) DESC")
    List<Object[]> topPayersByLandlord(@Param("landlordId") Long landlordId, org.springframework.data.domain.Pageable pageable);

    // Payment method breakdown
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.totalAmount) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' GROUP BY p.paymentMethod")
    List<Object[]> paymentMethodBreakdown();

    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.totalAmount) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' AND p.unit.property.landlord.id = :landlordId " +
           "GROUP BY p.paymentMethod")
    List<Object[]> paymentMethodBreakdownByLandlord(@Param("landlordId") Long landlordId);

    // Monthly trend with count
    @Query(value = "SELECT MONTH(paid_at), YEAR(paid_at), SUM(total_amount), COUNT(*) " +
                   "FROM payments WHERE status = 'COMPLETED' AND paid_at >= :from " +
                   "GROUP BY YEAR(paid_at), MONTH(paid_at) ORDER BY YEAR(paid_at), MONTH(paid_at)",
           nativeQuery = true)
    List<Object[]> monthlyTrendWithCount(@Param("from") LocalDateTime from);

    @Query(value = "SELECT MONTH(p.paid_at), YEAR(p.paid_at), SUM(p.total_amount), COUNT(*) " +
                   "FROM payments p LEFT JOIN units u ON u.id = p.unit_id " +
                   "LEFT JOIN properties prop ON prop.id = u.property_id " +
                   "WHERE p.status = 'COMPLETED' AND p.paid_at >= :from AND prop.landlord_id = :landlordId " +
                   "GROUP BY YEAR(p.paid_at), MONTH(p.paid_at) ORDER BY YEAR(p.paid_at), MONTH(p.paid_at)",
           nativeQuery = true)
    List<Object[]> monthlyTrendWithCountByLandlord(@Param("from") LocalDateTime from, @Param("landlordId") Long landlordId);

    @Query(value = "SELECT MONTH(p.paid_at) as month, YEAR(p.paid_at) as year, SUM(p.total_amount) as total " +
                   "FROM payments p " +
                   "LEFT JOIN units u ON u.id = p.unit_id " +
                   "LEFT JOIN properties prop ON prop.id = u.property_id " +
                   "WHERE p.status = 'COMPLETED' AND p.paid_at >= :from AND prop.landlord_id = :landlordId " +
                   "GROUP BY YEAR(p.paid_at), MONTH(p.paid_at) ORDER BY year, month",
           nativeQuery = true)
    List<Object[]> monthlyRevenueTrendByLandlord(@Param("from") LocalDateTime from,
                                                  @Param("landlordId") Long landlordId);
}
