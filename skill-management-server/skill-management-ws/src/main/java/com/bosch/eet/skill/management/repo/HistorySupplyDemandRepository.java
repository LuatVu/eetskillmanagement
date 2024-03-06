package com.bosch.eet.skill.management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.entity.HistorySupplyDemand;


public interface HistorySupplyDemandRepository extends JpaRepository<HistorySupplyDemand, String> {

    List<HistorySupplyDemand> findBySupplyDemandIdOrderByUpdatedDate(String id);
}
