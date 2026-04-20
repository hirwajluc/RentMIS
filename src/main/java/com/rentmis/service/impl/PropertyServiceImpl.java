package com.rentmis.service.impl;

import com.rentmis.audit.AuditService;
import com.rentmis.dto.request.PropertyRequest;
import com.rentmis.dto.request.UnitRequest;
import com.rentmis.dto.request.WingRequest;
import com.rentmis.dto.response.PropertyResponse;
import com.rentmis.dto.response.UnitResponse;
import com.rentmis.dto.response.WingResponse;
import com.rentmis.exception.RentMISException;
import com.rentmis.mapper.PropertyMapper;
import com.rentmis.mapper.UnitMapper;
import com.rentmis.mapper.WingMapper;
import com.rentmis.model.entity.Property;
import com.rentmis.model.entity.Unit;
import com.rentmis.model.entity.User;
import com.rentmis.model.entity.Wing;
import com.rentmis.model.enums.LandUse;
import com.rentmis.model.enums.PropertyCategory;
import com.rentmis.model.enums.Role;
import com.rentmis.model.enums.UnitStatus;
import com.rentmis.repository.PropertyRepository;
import com.rentmis.repository.UnitRepository;
import com.rentmis.repository.UserRepository;
import com.rentmis.repository.WingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyServiceImpl {

    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final WingRepository wingRepository;
    private final PropertyMapper propertyMapper;
    private final UnitMapper unitMapper;
    private final WingMapper wingMapper;
    private final AuditService auditService;

    // ── Property CRUD ───────────────────────────────────────────────────────────

    @Transactional
    public PropertyResponse createProperty(PropertyRequest request, Long landlordId) {
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> RentMISException.notFound("Landlord not found"));

        LandUse landUse = resolveLandUse(request);

        Property property = Property.builder()
                .category(request.getCategory())
                .landUse(landUse)
                .propertyType(request.getPropertyType())
                .name(request.getName())
                .address(request.getAddress())
                .province(request.getProvince())
                .city(request.getCity())
                .district(request.getDistrict())
                .sector(request.getSector())
                .cell(request.getCell())
                .village(request.getVillage())
                .upi(request.getUpi())
                .description(request.getDescription())
                .hasWings(request.getHasWings() != null ? request.getHasWings() : false)
                .numBedrooms(request.getNumBedrooms())
                .numBathrooms(request.getNumBathrooms())
                .parkingSpaces(request.getParkingSpaces())
                .houseRentAmount(request.getHouseRentAmount())
                .houseAreaSqm(request.getHouseAreaSqm())
                .totalAreaSqm(request.getTotalAreaSqm())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .landlord(landlord)
                .isActive(true)
                .build();

        property = propertyRepository.save(property);
        auditService.log("PROPERTY_CREATED", "Property", property.getId(), null);
        return enrichResponse(propertyMapper.toResponse(property), property.getId());
    }

    @Transactional
    public PropertyResponse updateProperty(Long id, PropertyRequest request, Long landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this property");
        }

        LandUse landUse = resolveLandUse(request);

        if (request.getCategory() != null)   property.setCategory(request.getCategory());
        if (landUse != null)                 property.setLandUse(landUse);
        if (request.getPropertyType() != null) property.setPropertyType(request.getPropertyType());

        property.setName(request.getName());
        property.setAddress(request.getAddress());
        property.setProvince(request.getProvince());
        property.setCity(request.getCity());
        property.setDistrict(request.getDistrict());
        property.setSector(request.getSector());
        property.setCell(request.getCell());
        property.setVillage(request.getVillage());
        property.setUpi(request.getUpi());
        property.setDescription(request.getDescription());

        if (request.getHasWings() != null) property.setHasWings(request.getHasWings());

        property.setNumBedrooms(request.getNumBedrooms());
        property.setNumBathrooms(request.getNumBathrooms());
        property.setParkingSpaces(request.getParkingSpaces());
        property.setHouseRentAmount(request.getHouseRentAmount());
        property.setHouseAreaSqm(request.getHouseAreaSqm());
        if (request.getTotalAreaSqm() != null) property.setTotalAreaSqm(request.getTotalAreaSqm());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());

        property = propertyRepository.save(property);
        auditService.log("PROPERTY_UPDATED", "Property", property.getId(), null);
        return enrichResponse(propertyMapper.toResponse(property), property.getId());
    }

    @Transactional
    public void deleteProperty(Long id, Long landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Property not found"));
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this property");
        }
        property.setIsActive(false);
        propertyRepository.save(property);
        auditService.log("PROPERTY_DELETED", "Property", id, null);
    }

    @Transactional(readOnly = true)
    public Page<PropertyResponse> getProperties(Long landlordId, String search, Pageable pageable) {
        return propertyRepository.searchProperties(landlordId, search != null ? search : "", pageable)
                .map(p -> enrichResponse(propertyMapper.toResponse(p), p.getId()));
    }

    @Transactional(readOnly = true)
    public PropertyResponse getProperty(Long id) {
        Property p = propertyRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Property not found"));
        return enrichResponse(propertyMapper.toResponse(p), id);
    }

    // ── Wing management ─────────────────────────────────────────────────────────

    @Transactional
    public WingResponse createWing(WingRequest request, Long landlordId) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> RentMISException.notFound("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this property");
        }

        if (wingRepository.existsByPropertyIdAndNameIgnoreCase(property.getId(), request.getName())) {
            throw RentMISException.conflict("A wing with this name already exists for this property");
        }

        Wing wing = Wing.builder()
                .name(request.getName())
                .description(request.getDescription())
                .property(property)
                .isActive(true)
                .build();

        wing = wingRepository.save(wing);

        // Make sure property is flagged as having wings
        if (!Boolean.TRUE.equals(property.getHasWings())) {
            property.setHasWings(true);
            propertyRepository.save(property);
        }

        auditService.log("WING_CREATED", "Wing", wing.getId(), null);
        return toWingResponse(wing);
    }

    @Transactional
    public WingResponse updateWing(Long wingId, WingRequest request, Long landlordId) {
        Wing wing = wingRepository.findById(wingId)
                .orElseThrow(() -> RentMISException.notFound("Wing not found"));

        if (!wing.getProperty().getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this wing");
        }

        wing.setName(request.getName());
        wing.setDescription(request.getDescription());
        wing = wingRepository.save(wing);

        auditService.log("WING_UPDATED", "Wing", wingId, null);
        return toWingResponse(wing);
    }

    @Transactional
    public void deleteWing(Long wingId, Long landlordId) {
        Wing wing = wingRepository.findById(wingId)
                .orElseThrow(() -> RentMISException.notFound("Wing not found"));

        if (!wing.getProperty().getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this wing");
        }

        wing.setIsActive(false);
        wingRepository.save(wing);
        auditService.log("WING_DELETED", "Wing", wingId, null);
    }

    @Transactional(readOnly = true)
    public List<WingResponse> getWingsByProperty(Long propertyId) {
        return wingRepository.findByPropertyIdAndIsActiveTrueOrderByNameAsc(propertyId)
                .stream().map(this::toWingResponse).collect(Collectors.toList());
    }

    // ── Unit management ─────────────────────────────────────────────────────────

    @Transactional
    public UnitResponse createUnit(UnitRequest request, Long landlordId) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> RentMISException.notFound("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this property");
        }

        unitRepository.findByPropertyIdAndUnitNumber(property.getId(), request.getUnitNumber())
                .ifPresent(u -> { throw RentMISException.conflict("Unit number already exists"); });

        Wing wing = null;
        if (request.getWingId() != null) {
            wing = wingRepository.findById(request.getWingId())
                    .filter(w -> w.getProperty().getId().equals(property.getId()))
                    .orElseThrow(() -> RentMISException.notFound("Wing not found on this property"));
        }

        validateAreaAvailable(property, request.getAreaSqm(), request.getFloorNumber(), null);

        BigDecimal rentAmount = resolveRentAmount(request, property);

        Unit unit = Unit.builder()
                .unitNumber(request.getUnitNumber())
                .floorNumber(request.getFloorNumber())
                .unitType(request.getUnitType())
                .unitPurpose(request.getUnitPurpose())
                .rentAmount(rentAmount)
                .depositAmount(request.getDepositAmount())
                .areaSqm(request.getAreaSqm())
                .pricePerSqm(request.getPricePerSqm())
                .numBedrooms(request.getNumBedrooms())
                .numBathrooms(request.getNumBathrooms())
                .amenities(request.getAmenities())
                .property(property)
                .wing(wing)
                .status(UnitStatus.AVAILABLE)
                .isActive(true)
                .build();

        unit = unitRepository.save(unit);

        // Keep derived totalUnits counter in sync
        syncTotalUnits(property);

        auditService.log("UNIT_CREATED", "Unit", unit.getId(), null);
        return unitMapper.toResponse(unit);
    }

    @Transactional
    public UnitResponse updateUnit(Long id, UnitRequest request, Long landlordId) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> RentMISException.notFound("Unit not found"));

        if (!unit.getProperty().getLandlord().getId().equals(landlordId)) {
            throw RentMISException.forbidden("You do not own this unit");
        }

        final Long unitPropertyId = unit.getProperty().getId();
        Wing wing = null;
        if (request.getWingId() != null) {
            wing = wingRepository.findById(request.getWingId())
                    .filter(w -> w.getProperty().getId().equals(unitPropertyId))
                    .orElseThrow(() -> RentMISException.notFound("Wing not found on this property"));
        }

        validateAreaAvailable(unit.getProperty(), request.getAreaSqm(), request.getFloorNumber(), unit.getId());

        BigDecimal rentAmount = resolveRentAmount(request, unit.getProperty());

        unit.setUnitNumber(request.getUnitNumber());
        unit.setFloorNumber(request.getFloorNumber());
        unit.setUnitType(request.getUnitType());
        unit.setUnitPurpose(request.getUnitPurpose());
        unit.setRentAmount(rentAmount);
        unit.setDepositAmount(request.getDepositAmount());
        unit.setAreaSqm(request.getAreaSqm());
        unit.setPricePerSqm(request.getPricePerSqm());
        unit.setNumBedrooms(request.getNumBedrooms());
        unit.setNumBathrooms(request.getNumBathrooms());
        unit.setAmenities(request.getAmenities());
        unit.setWing(wing);

        unit = unitRepository.save(unit);
        auditService.log("UNIT_UPDATED", "Unit", id, null);
        return unitMapper.toResponse(unit);
    }

    /**
     * For COMPLEX properties: if pricePerSqm and areaSqm are both provided,
     * compute rent = pricePerSqm × areaSqm. Otherwise use the manually entered rent.
     */
    private BigDecimal resolveRentAmount(UnitRequest request, Property property) {
        if (property.getCategory() == com.rentmis.model.enums.PropertyCategory.COMPLEX
                && request.getPricePerSqm() != null
                && request.getAreaSqm() != null) {
            return request.getPricePerSqm()
                    .multiply(request.getAreaSqm())
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return request.getRentAmount();
    }

    @Transactional(readOnly = true)
    public Page<UnitResponse> getUnits(Long propertyId, Long landlordId, Pageable pageable) {
        if (propertyId != null) {
            return unitRepository.findByPropertyIdAndIsActiveTrue(propertyId, pageable)
                    .map(unitMapper::toResponse);
        }
        return unitRepository.findByLandlordId(landlordId, pageable)
                .map(unitMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UnitResponse getUnit(Long id) {
        return unitMapper.toResponse(
                unitRepository.findById(id)
                        .orElseThrow(() -> RentMISException.notFound("Unit not found")));
    }

    @Transactional(readOnly = true)
    public UnitResponse getUnitByCurrentTenant(Long tenantId) {
        return unitRepository.findByCurrentTenantId(tenantId).stream()
                .findFirst()
                .map(unitMapper::toResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<UnitResponse> getAvailableUnits(Long landlordId) {
        List<Unit> units = landlordId != null
                ? unitRepository.findAvailableByLandlordId(landlordId)
                : unitRepository.findAllAvailable();
        return units.stream().map(unitMapper::toResponse).collect(Collectors.toList());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    /**
     * Auto-derive LandUse from category; let an explicit override win.
     */
    private LandUse resolveLandUse(PropertyRequest request) {
        if (request.getLandUse() != null) return request.getLandUse();
        if (request.getCategory() == null) return null;
        return switch (request.getCategory()) {
            case FULL_HOUSE, APARTMENT_BUILDING -> LandUse.RESIDENTIAL;
            case COMPLEX -> LandUse.MIXED;
        };
    }

    /**
     * Keep the denormalised totalUnits counter in sync after insert/delete.
     */
    private void syncTotalUnits(Property property) {
        long count = unitRepository.countByPropertyId(property.getId());
        property.setTotalUnits((int) count);
        propertyRepository.save(property);
    }

    /**
     * Populate derived fields that cannot come from the mapper alone.
     */
    private PropertyResponse enrichResponse(PropertyResponse r, Long propertyId) {
        long occupied = unitRepository.countByPropertyIdAndStatus(propertyId, UnitStatus.OCCUPIED);
        long total    = unitRepository.countByPropertyId(propertyId);
        r.setOccupiedUnits(occupied);
        r.setAvailableUnits(total - occupied);
        r.setTotalUnits((int) total);

        Integer maxFloor = unitRepository.findMaxFloorByPropertyId(propertyId);
        r.setFloors(maxFloor);

        BigDecimal usedArea = unitRepository.sumAreaSqmByPropertyId(propertyId);
        r.setUsedAreaSqm(usedArea);

        return r;
    }

    /**
     * Validates that a unit's area does not exceed the property's totalAreaSqm for its floor.
     * The totalAreaSqm is the allowed area per floor (same for every floor).
     * Passes silently if no total area is configured or no floor is specified.
     */
    private void validateAreaAvailable(Property property, BigDecimal requestedArea,
                                       Integer floorNumber, Long excludeUnitId) {
        if (property.getTotalAreaSqm() == null || requestedArea == null || floorNumber == null) return;

        BigDecimal alreadyUsed = excludeUnitId != null
                ? unitRepository.sumAreaSqmByPropertyIdAndFloorExcludingUnit(property.getId(), floorNumber, excludeUnitId)
                : unitRepository.sumAreaSqmByPropertyIdAndFloor(property.getId(), floorNumber);

        BigDecimal available = property.getTotalAreaSqm().subtract(alreadyUsed);
        if (requestedArea.compareTo(available) > 0) {
            throw RentMISException.badRequest(
                String.format("Insufficient area on floor %d: %.2f m² requested but only %.2f m² available out of %.2f m² per floor",
                        floorNumber, requestedArea, available, property.getTotalAreaSqm()));
        }
    }

    /**
     * Returns used area on a specific floor of a property, and the total area per floor.
     * Used by the frontend live-area meter.
     */
    public java.util.Map<String, Object> getFloorAreaInfo(Long propertyId, Integer floorNumber, Long excludeUnitId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> RentMISException.notFound("Property not found"));

        BigDecimal total = property.getTotalAreaSqm();
        BigDecimal used = (floorNumber != null)
                ? (excludeUnitId != null
                        ? unitRepository.sumAreaSqmByPropertyIdAndFloorExcludingUnit(propertyId, floorNumber, excludeUnitId)
                        : unitRepository.sumAreaSqmByPropertyIdAndFloor(propertyId, floorNumber))
                : java.math.BigDecimal.ZERO;

        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("totalAreaSqm", total);
        result.put("usedAreaSqm", used);
        result.put("availableAreaSqm", total != null ? total.subtract(used) : null);
        result.put("floorNumber", floorNumber);
        return result;
    }

    private WingResponse toWingResponse(Wing wing) {
        WingResponse r = wingMapper.toResponse(wing);
        long count = unitRepository.countByWingIdAndIsActiveTrue(wing.getId());
        r.setUnitCount((int) count);
        return r;
    }
}
