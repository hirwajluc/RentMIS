package com.rentmis.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class ReportData {

    private String generatedAt;
    private String generatedBy;
    private String period;          // e.g. "2025" or "Jan 2025 – Apr 2026"

    // ── Financial summary ─────────────────────────────────────────────────────
    private BigDecimal totalRevenue;
    private BigDecimal annualRevenue;
    private BigDecimal monthlyRevenue;
    private Long completedPayments;
    private Long pendingPayments;
    private Long failedPayments;
    private BigDecimal avgPaymentAmount;

    // ── Occupancy ─────────────────────────────────────────────────────────────
    private Long totalUnits;
    private Long occupiedUnits;
    private Long availableUnits;
    private Double occupancyRate;
    private Long totalProperties;

    // ── Contracts ─────────────────────────────────────────────────────────────
    private Long activeContracts;
    private Long expiredContracts;
    private Long terminatedContracts;
    private Long pendingSignatureContracts;
    private Long totalContracts;

    // ── Tenants / Landlords ───────────────────────────────────────────────────
    private Long totalTenants;
    private Long totalLandlords;

    // ── Invoices / EBM ────────────────────────────────────────────────────────
    private Long totalInvoices;
    private Long ebmSuccess;
    private Long ebmPending;
    private Long ebmFailed;

    // ── Monthly trend (last 12 months) ────────────────────────────────────────
    private List<MonthRow> monthlyTrend;

    // ── Top payers ────────────────────────────────────────────────────────────
    private List<TopPayer> topPayers;

    // ── Payment method breakdown ──────────────────────────────────────────────
    private List<MethodRow> paymentMethods;

    @Data @Builder
    public static class MonthRow {
        private String label;
        private BigDecimal revenue;
        private Long count;
    }

    @Data @Builder
    public static class TopPayer {
        private String tenantName;
        private Long paymentCount;
        private BigDecimal totalPaid;
    }

    @Data @Builder
    public static class MethodRow {
        private String method;
        private Long count;
        private BigDecimal total;
    }
}
