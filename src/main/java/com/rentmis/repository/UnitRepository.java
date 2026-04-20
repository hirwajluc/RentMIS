package com.rentmis.repository;

import com.rentmis.model.entity.Unit;
import com.rentmis.model.enums.UnitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    Page<Unit> findByPropertyIdAndIsActiveTrue(Long propertyId, Pageable pageable);

    List<Unit> findByPropertyIdAndIsActiveTrue(Long propertyId);

    List<Unit> findByPropertyIdAndStatus(Long propertyId, UnitStatus status);

    Optional<Unit> findByPropertyIdAndUnitNumber(Long propertyId, String unitNumber);

    List<Unit> findByCurrentTenantId(Long tenantId);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.property.id = :propertyId AND u.isActive = true")
    Long countByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.property.id = :propertyId " +
           "AND u.status = :status AND u.isActive = true")
    Long countByPropertyIdAndStatus(@Param("propertyId") Long propertyId,
                                    @Param("status") UnitStatus status);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.status = 'OCCUPIED' AND u.isActive = true")
    Long countOccupiedUnits();

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.isActive = true")
    Long countActiveUnits();

    @Query("SELECT u FROM Unit u WHERE u.property.landlord.id = :landlordId AND u.isActive = true")
    Page<Unit> findByLandlordId(@Param("landlordId") Long landlordId, Pageable pageable);

    @Query("SELECT u FROM Unit u WHERE u.property.landlord.id = :landlordId " +
           "AND u.status = 'AVAILABLE' AND u.isActive = true ORDER BY u.property.name, u.unitNumber")
    List<Unit> findAvailableByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT u FROM Unit u WHERE u.status = 'AVAILABLE' AND u.isActive = true " +
           "ORDER BY u.property.name, u.unitNumber")
    List<Unit> findAllAvailable();

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.property.landlord.id = :landlordId AND u.isActive = true")
    Long countByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.property.landlord.id = :landlordId " +
           "AND u.status = 'OCCUPIED' AND u.isActive = true")
    Long countOccupiedByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT MAX(u.floorNumber) FROM Unit u WHERE u.property.id = :propertyId AND u.isActive = true")
    Integer findMaxFloorByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT COALESCE(SUM(u.areaSqm), 0) FROM Unit u WHERE u.property.id = :propertyId AND u.isActive = true")
    java.math.BigDecimal sumAreaSqmByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT COALESCE(SUM(u.areaSqm), 0) FROM Unit u WHERE u.property.id = :propertyId AND u.floorNumber = :floor AND u.isActive = true")
    java.math.BigDecimal sumAreaSqmByPropertyIdAndFloor(@Param("propertyId") Long propertyId, @Param("floor") Integer floor);

    @Query("SELECT COALESCE(SUM(u.areaSqm), 0) FROM Unit u WHERE u.property.id = :propertyId AND u.floorNumber = :floor AND u.isActive = true AND u.id <> :excludeUnitId")
    java.math.BigDecimal sumAreaSqmByPropertyIdAndFloorExcludingUnit(@Param("propertyId") Long propertyId, @Param("floor") Integer floor, @Param("excludeUnitId") Long excludeUnitId);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.wing.id = :wingId AND u.isActive = true")
    Long countByWingIdAndIsActiveTrue(@Param("wingId") Long wingId);
}
