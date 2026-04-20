package com.rentmis.controller;

import com.rentmis.dto.request.UnitRequest;
import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.UnitResponse;
import com.rentmis.security.jwt.CustomUserDetails;
import com.rentmis.service.impl.PropertyServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final PropertyServiceImpl propertyService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UnitResponse>>> listUnits(
            @RequestParam(required = false) Long propertyId,
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        Long landlordId = user.getRole().name().equals("LANDLORD") ? user.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.ok(
                propertyService.getUnits(propertyId, landlordId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnitResponse>> getUnit(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(propertyService.getUnit(id)));
    }

    @GetMapping("/my-unit")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponse<UnitResponse>> getMyUnit(
            @AuthenticationPrincipal CustomUserDetails user) {
        UnitResponse unit = propertyService.getUnitByCurrentTenant(user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok(unit));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<List<UnitResponse>>> getAvailableUnits(
            @AuthenticationPrincipal CustomUserDetails user) {
        Long landlordId = "LANDLORD".equals(user.getRole().name()) ? user.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.ok(propertyService.getAvailableUnits(landlordId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<UnitResponse>> createUnit(
            @Valid @RequestBody UnitRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Unit created",
                        propertyService.createUnit(request, user.getUserId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<UnitResponse>> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Unit updated",
                propertyService.updateUnit(id, request, user.getUserId())));
    }
}
