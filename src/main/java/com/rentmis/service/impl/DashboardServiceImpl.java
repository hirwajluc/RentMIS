package com.rentmis.service.impl;

import com.rentmis.dto.response.DashboardResponse;
import com.rentmis.model.enums.Role;
import com.rentmis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl {

    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long landlordId) {
        boolean isAdmin = landlordId == null;

        long totalProperties = isAdmin
                ? propertyRepository.countActiveProperties()
                : propertyRepository.countByLandlordId(landlordId);

        long totalUnits;
        long occupiedUnits;
        if (isAdmin) {
            totalUnits    = unitRepository.countActiveUnits();
            occupiedUnits = unitRepository.countOccupiedUnits();
        } else {
            totalUnits    = unitRepository.countByLandlordId(landlordId);
            occupiedUnits = unitRepository.countOccupiedByLandlordId(landlordId);
        }
        long availableUnits = totalUnits - occupiedUnits;

        long totalTenants = isAdmin
                ? userRepository.countByRoleAndActive(Role.TENANT)
                : userRepository.countTenantsByLandlordId(landlordId);

        long totalLandlords = isAdmin ? userRepository.countByRoleAndActive(Role.LANDLORD) : 0L;

        long activeContracts = isAdmin
                ? contractRepository.countActiveContracts()
                : contractRepository.countActiveContractsByLandlordId(landlordId);

        LocalDateTime now = LocalDateTime.now();

        BigDecimal totalRevenue = isAdmin
                ? paymentRepository.totalRevenue()
                : paymentRepository.totalRevenueByLandlord(landlordId);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal monthlyRevenue = isAdmin
                ? paymentRepository.sumCompletedByMonthYear(now.getMonthValue(), now.getYear())
                : paymentRepository.sumCompletedByMonthYearAndLandlord(now.getMonthValue(), now.getYear(), landlordId);
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;

        LocalDateTime yearStart = now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal annualRevenue = isAdmin
                ? paymentRepository.sumCompletedPayments(yearStart, now)
                : paymentRepository.sumCompletedPaymentsByLandlord(yearStart, now, landlordId);
        if (annualRevenue == null) annualRevenue = BigDecimal.ZERO;

        Long completedPaymentsL = isAdmin
                ? paymentRepository.countCompletedPayments()
                : paymentRepository.countCompletedPaymentsByLandlord(landlordId);
        Long pendingPaymentsL = isAdmin
                ? paymentRepository.countPendingPayments()
                : paymentRepository.countPendingPaymentsByLandlord(landlordId);

        double occupancyRate = totalUnits > 0
                ? (double) occupiedUnits / totalUnits * 100.0 : 0.0;

        // Monthly trend - last 12 months
        List<Object[]> trend = isAdmin
                ? paymentRepository.monthlyRevenueTrend(now.minusMonths(12))
                : paymentRepository.monthlyRevenueTrendByLandlord(now.minusMonths(12), landlordId);
        List<DashboardResponse.MonthlyRevenue> monthlyTrend = new ArrayList<>();
        for (Object[] row : trend) {
            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            BigDecimal total = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            String label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
            monthlyTrend.add(DashboardResponse.MonthlyRevenue.builder()
                    .month(month).year(year).total(total).label(label).build());
        }

        return DashboardResponse.builder()
                .totalProperties(totalProperties)
                .totalUnits(totalUnits)
                .occupiedUnits(occupiedUnits)
                .availableUnits(availableUnits)
                .totalTenants(totalTenants)
                .totalLandlords(totalLandlords)
                .activeContracts(activeContracts)
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .annualRevenue(annualRevenue)
                .completedPayments(completedPaymentsL != null ? completedPaymentsL : 0L)
                .pendingPayments(pendingPaymentsL != null ? pendingPaymentsL : 0L)
                .occupancyRate(BigDecimal.valueOf(occupancyRate)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue())
                .monthlyTrend(monthlyTrend)
                .build();
    }
}
