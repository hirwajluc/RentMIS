package com.rentmis.controller;

import com.rentmis.dto.request.PropertyRequest;
import com.rentmis.dto.request.WingRequest;
import com.rentmis.dto.response.ApiResponse;
import com.rentmis.dto.response.PropertyResponse;
import com.rentmis.dto.response.WingResponse;
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
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyServiceImpl propertyService;

    // ── Properties ──────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PropertyResponse>>> listProperties(
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal CustomUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        Long landlordId = "LANDLORD".equals(user.getRole().name()) ? user.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.ok(propertyService.getProperties(landlordId, search, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyResponse>> getProperty(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(propertyService.getProperty(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<PropertyResponse>> createProperty(
            @Valid @RequestBody PropertyRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Property created", propertyService.createProperty(request, user.getUserId())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<PropertyResponse>> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Property updated",
                propertyService.updateProperty(id, request, user.getUserId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user) {
        propertyService.deleteProperty(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Property deleted", null));
    }

    // ── Floor area info (for live area meter in frontend) ───────────────────────

    @GetMapping("/{propertyId}/floor-area")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getFloorArea(
            @PathVariable Long propertyId,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Long excludeUnitId) {
        return ResponseEntity.ok(ApiResponse.ok(
                propertyService.getFloorAreaInfo(propertyId, floor, excludeUnitId)));
    }

    // ── Wings ───────────────────────────────────────────────────────────────────

    @GetMapping("/{propertyId}/wings")
    public ResponseEntity<ApiResponse<List<WingResponse>>> getWings(@PathVariable Long propertyId) {
        return ResponseEntity.ok(ApiResponse.ok(propertyService.getWingsByProperty(propertyId)));
    }

    @PostMapping("/wings")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<WingResponse>> createWing(
            @Valid @RequestBody WingRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Wing created", propertyService.createWing(request, user.getUserId())));
    }

    @PutMapping("/wings/{wingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<WingResponse>> updateWing(
            @PathVariable Long wingId,
            @Valid @RequestBody WingRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Wing updated",
                propertyService.updateWing(wingId, request, user.getUserId())));
    }

    @DeleteMapping("/wings/{wingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<ApiResponse<Void>> deleteWing(
            @PathVariable Long wingId,
            @AuthenticationPrincipal CustomUserDetails user) {
        propertyService.deleteWing(wingId, user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Wing deleted", null));
    }
}
