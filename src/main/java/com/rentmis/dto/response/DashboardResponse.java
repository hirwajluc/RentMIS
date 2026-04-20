package com.rentmis.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder
public class DashboardResponse {
    // Counts
    private Long totalProperties;
    private Long totalUnits;
    private Long occupiedUnits;
    private Long availableUnits;
    private Long totalTenants;
    private Long totalLandlords;
    private Long activeContracts;

    // Financial
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal annualRevenue;
    private Long completedPayments;
    private Long pendingPayments;

    // Rates
    private Double occupancyRate;

    // Trends
    private List<MonthlyRevenue> monthlyTrend;

    @Data @Builder
    public static class MonthlyRevenue {
        private int month;
        private int year;
        private BigDecimal total;
        private String label;
    }
}
