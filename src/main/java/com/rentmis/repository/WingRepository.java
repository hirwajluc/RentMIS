package com.rentmis.repository;

import com.rentmis.model.entity.Wing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WingRepository extends JpaRepository<Wing, Long> {

    List<Wing> findByPropertyIdAndIsActiveTrueOrderByNameAsc(Long propertyId);

    boolean existsByPropertyIdAndNameIgnoreCase(Long propertyId, String name);
}
