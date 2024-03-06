package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.entity.Team;

public interface TeamRepository extends JpaRepository<Team, String>, JpaSpecificationExecutor {

    Optional<Team> findByName(String name);

//    @Query("select distinct t from Team t" +
//            " join Personal p on p.team = t.id" +
//            " group by t.name" +
//            " order by count(distinct p.id) desc")
//    Page<Team> findTopTeams(Pageable pageable);

    @Query("select distinct new com.bosch.eet.skill.management.dto.TeamDto(t.name," +
            " cast(count(distinct p.id) as int) as associates)" +
            " from Team t join Personal p on p.team.id = t.id" +
            " where :team is null or t.name = :team" +
            " group by t.name" +
            " order by count(distinct p.id) desc")
    Page<TeamDto> findTopTeams(String team, Pageable pageable);
    
    List<Team> findByOrderByName();

}
