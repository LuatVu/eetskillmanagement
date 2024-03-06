package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.SupplyDemand;


public interface SupplyDemandRepository extends JpaRepository<SupplyDemand, String> {
//    @Query("SELECT sd FROM SupplyDemand sd " +
//            "WHERE (sd.itemId, sd.versionId) IN " +
//            "(SELECT s.itemId, MAX(s.versionId) FROM SupplyDemand s GROUP BY s.itemId)")
//    List<SupplyDemand> findAllWithMaxVersion();

    List<SupplyDemand> findAllById(String id);

    @Query(value = "SELECT COALESCE(MAX(subId), 0) FROM SupplyDemand")
    Long getMaxIdDemand();
    
    Optional<SupplyDemand> findBySubId(Long subId);
}
