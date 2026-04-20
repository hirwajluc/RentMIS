package com.rentmis.service.impl;

import com.rentmis.dto.response.TenantReportResponse;
import com.rentmis.dto.response.UserResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.mapper.UserMapper;
import com.rentmis.model.entity.TenantReport;
import com.rentmis.model.entity.User;
import com.rentmis.model.enums.Language;
import com.rentmis.model.enums.ReportStatus;
import com.rentmis.model.enums.Role;
import com.rentmis.repository.TenantReportRepository;
import com.rentmis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final TenantReportRepository tenantReportRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("User not found")));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Role role, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return userRepository.searchByRole(role, search, pageable).map(userMapper::toResponse);
        }
        if (role != null) {
            return userRepository.findByRole(role, pageable).map(userMapper::toResponse);
        }
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Transactional
    public UserResponse toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("User not found"));
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> RentMISException.notFound("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw RentMISException.badRequest("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, String firstName, String lastName,
                                       String phone, String address, Language language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> RentMISException.notFound("User not found"));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phone != null) user.setPhone(phone);
        if (address != null) user.setAddress(address);
        if (language != null) user.setLanguage(language);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAvailableTenants() {
        LocalDate now = LocalDate.now();
        int currentPeriod = now.getYear() * 12 + now.getMonthValue();

        java.util.Set<Long> reportedIds = userRepository.findTenantIdsWithVerifiedReport();
        java.util.Set<Long> overdueIds  = userRepository.findTenantIdsWithOverduePayments(currentPeriod);

        return userRepository.findAllActiveTenants().stream().map(user -> {
            UserResponse r = userMapper.toResponse(user);
            java.util.List<String> reasons = new java.util.ArrayList<>();
            if (reportedIds.contains(user.getId()))
                reasons.add("Verified report on record");
            if (overdueIds.contains(user.getId()))
                reasons.add("Overdue payments");
            r.setFlagged(!reasons.isEmpty());
            r.setFlagReasons(reasons.isEmpty() ? null : reasons);
            return r;
        }).collect(java.util.stream.Collectors.toList());
    }

    // ── Tenant reporting ──────────────────────────────────────────────────────

    @Transactional
    public TenantReportResponse reportTenant(Long tenantId, Long landlordId, String reason) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> RentMISException.notFound("Tenant not found"));
        if (tenant.getRole() != Role.TENANT) {
            throw RentMISException.badRequest("User is not a tenant");
        }
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> RentMISException.notFound("Landlord not found"));

        TenantReport report = TenantReport.builder()
                .tenant(tenant)
                .reportedBy(landlord)
                .reason(reason)
                .status(ReportStatus.PENDING)
                .build();

        return toReportResponse(tenantReportRepository.save(report));
    }

    @Transactional
    public TenantReportResponse reviewReport(Long reportId, Long adminId, ReportStatus decision, String notes) {
        TenantReport report = tenantReportRepository.findById(reportId)
                .orElseThrow(() -> RentMISException.notFound("Report not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> RentMISException.notFound("Admin not found"));

        report.setStatus(decision);
        report.setReviewedBy(admin);
        report.setAdminNotes(notes);
        report.setReviewedAt(LocalDateTime.now());
        return toReportResponse(tenantReportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public Page<TenantReportResponse> getReports(ReportStatus status, Pageable pageable) {
        if (status != null) {
            return tenantReportRepository.findByStatus(status, pageable).map(this::toReportResponse);
        }
        return tenantReportRepository.findAll(pageable).map(this::toReportResponse);
    }

    @Transactional(readOnly = true)
    public List<TenantReportResponse> getReportsForTenant(Long tenantId) {
        return tenantReportRepository.findByTenantId(tenantId).stream()
                .map(this::toReportResponse).collect(Collectors.toList());
    }

    private TenantReportResponse toReportResponse(TenantReport r) {
        TenantReportResponse dto = new TenantReportResponse();
        dto.setId(r.getId());
        dto.setTenantId(r.getTenant().getId());
        dto.setTenantName(r.getTenant().getFirstName() + " " + r.getTenant().getLastName());
        dto.setTenantEmail(r.getTenant().getEmail());
        dto.setReportedById(r.getReportedBy().getId());
        dto.setReportedByName(r.getReportedBy().getFirstName() + " " + r.getReportedBy().getLastName());
        dto.setReason(r.getReason());
        dto.setStatus(r.getStatus());
        dto.setAdminNotes(r.getAdminNotes());
        dto.setReviewedAt(r.getReviewedAt());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
