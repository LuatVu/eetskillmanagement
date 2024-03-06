package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.LevelDto;
import com.bosch.eet.skill.management.entity.Level;


public interface LevelRepository extends JpaRepository<Level, String> {

    @Query("select distinct new com.bosch.eet.skill.management.dto.LevelDto(l.name," +
            " cast(count(distinct p.id) as int) as associates)" +
            " from Level l join Personal p on p.level.id = l.id" +
            " join Team t on p.team.id = t.id" +
            " where :team is null or t.name = :team" +
            " group by l.name" +
            " order by count(distinct p.id) desc")
    List<LevelDto> findAllLevels(String team);

    Optional<Level> findLevelById(String id);

    Optional<Level> findLevelByName(String levelName);

    Optional<Level> findByName(String name);
    
    List<Level> findByOrderByName();
}

