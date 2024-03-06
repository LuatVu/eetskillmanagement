package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.GbUnitDto;
import com.bosch.eet.skill.management.entity.GbUnit;

public interface GbUnitRepository extends JpaRepository<GbUnit, String>, JpaSpecificationExecutor {
    Optional<GbUnit> findByName(String name);

    @Query("select distinct new com.bosch.eet.skill.management.dto.GbUnitDto(gbu.name," +
            " cast(count(distinct pp.personal.id) as int) as associates, cast(null as int) as projects)" +
            " from GbUnit gbu join Project p on p.gbUnit.id = gbu.id" +
            " join PersonalProject pp on pp.project.id = p.id" +
            " join Personal p on pp.personal.id = p.id" +
            " join Team t on p.team.id = t.id" +
            " where :team is null or t.name = :team" +
            " group by gbu.name" +
            " order by count(distinct pp.personal.id) desc")
    Page<GbUnitDto> findTopGbAssociates(String team, Pageable pageable);

    @Query("select distinct new com.bosch.eet.skill.management.dto.GbUnitDto(gbu.name," +
            " cast(null as int) as associates, cast(count(distinct p.id) as int) as projects)" +
            " from GbUnit gbu join Project p on p.gbUnit.id = gbu.id" +
            " where :gbUnit is null or gbu.name = :gbUnit" +
            " group by gbu.name" +
            " order by count(distinct p.id) desc")
    Page<GbUnitDto> findTopGbProjects(String gbUnit, Pageable pageable);

}