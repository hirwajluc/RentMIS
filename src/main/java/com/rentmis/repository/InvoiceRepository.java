package com.rentmis.repository;

import com.rentmis.model.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByPaymentId(Long paymentId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByEbmInvoiceNumber(String ebmInvoiceNumber);

    Page<Invoice> findByPaymentTenantId(Long tenantId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:tenantId IS NULL OR i.payment.tenant.id = :tenantId) AND " +
           "(:ebmStatus IS NULL OR i.ebmStatus = :ebmStatus)")
    Page<Invoice> filterInvoices(@Param("tenantId") Long tenantId,
                                 @Param("ebmStatus") String ebmStatus,
                                 Pageable pageable);

    long countByEbmStatus(String ebmStatus);

    @Query("SELECT COUNT(i) FROM Invoice i JOIN i.payment p JOIN p.unit u JOIN u.property prop WHERE prop.landlord.id = :landlordId")
    long countByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(i) FROM Invoice i JOIN i.payment p JOIN p.unit u JOIN u.property prop WHERE prop.landlord.id = :landlordId AND i.ebmStatus = :ebmStatus")
    long countByLandlordIdAndEbmStatus(@Param("landlordId") Long landlordId, @Param("ebmStatus") String ebmStatus);
}
