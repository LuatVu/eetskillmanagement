package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;

public interface SkillRepository extends JpaRepository<Skill, String>, JpaSpecificationExecutor {
    @Query("select distinct new Skill (s.name) from Skill s")
    List<Skill> findNameList();

    @Query("select distinct new com.bosch.eet.skill.management.dto.SkillDto(s.name," +
            " cast(count(distinct ps.personal.id) as int) as associates)" +
            " from Skill s join PersonalSkill ps on ps.skill.id in s" +
            " join Personal p on ps.personal.id = p.id" +
            " join Team t on p.team.id = t.id" +
            " where :team is null or t.name = :team" +
            " group by s.name" +
            " order by count(distinct ps.personal.id) desc")
    Page<SkillDto> findTopSkills(String team, Pageable pageable);

    Optional<Skill> findByName(String name);
    
    Optional<Skill> findByNameAndSkillGroup(String name, SkillGroup skillGroup);
    
    List<Skill> findAllBySkillGroup (SkillGroup skillgroup);

    List<Skill> findAllByIdIn(List<String> allSkillsESIds);

    List<Skill> findAllByName(String name);
    
    @Query("select count(id) from Skill")
    Integer countTotal();
    
    @Query("select s from Skill s where s.skillGroup.name is null or s.skillGroup=:skillGroup")
    Page<Skill> findAllFilterSkillGroup(SkillGroup skillGroup,Pageable pageable);
}
