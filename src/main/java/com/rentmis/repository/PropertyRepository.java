package com.rentmis.repository;

import com.rentmis.model.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    Page<Property> findByLandlordIdAndIsActiveTrue(Long landlordId, Pageable pageable);

    List<Property> findByLandlordIdAndIsActiveTrue(Long landlordId);

    @Query("SELECT p FROM Property p WHERE p.isActive = true " +
           "AND (:landlordId IS NULL OR p.landlord.id = :landlordId) " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.city) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Property> searchProperties(@Param("landlordId") Long landlordId,
                                    @Param("search") String search,
                                    Pageable pageable);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.landlord.id = :landlordId AND p.isActive = true")
    Long countByLandlordId(@Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(p) FROM Property p WHERE p.isActive = true")
    Long countActiveProperties();

    /**
     * Returns active properties that have at least one AVAILABLE unit.
     * Used for the agent's vacant-property browse view.
     * No landlord data is returned — projection is done in the service layer.
     */
    @Query("SELECT DISTINCT p FROM Property p " +
           "JOIN p.units u " +
           "WHERE p.isActive = true " +
           "AND u.isActive = true AND u.status = 'AVAILABLE' " +
           "AND (:search IS NULL OR :search = '' " +
           "  OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.district) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.province) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Property> findVacantProperties(@Param("search") String search, Pageable pageable);
}
