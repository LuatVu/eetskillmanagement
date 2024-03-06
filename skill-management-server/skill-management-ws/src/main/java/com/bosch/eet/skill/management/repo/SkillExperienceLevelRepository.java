package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;

public interface SkillExperienceLevelRepository extends JpaRepository<SkillExperienceLevel, String>, JpaSpecificationExecutor {
    Optional<SkillExperienceLevel> findByName(String name);
    List<SkillExperienceLevel> findBySkillAndLevel(Skill skill, Level level);
    Optional<SkillExperienceLevel> findByNameAndSkillIdAndSkillGroupId(String name, String skill, String skillGroup);
    List<SkillExperienceLevel> findNameAndDescriptionDistinctBySkillId(String skillId);
    @Query("select distinct new SkillExperienceLevel(s.name, s.description) from SkillExperienceLevel s where s.skill.id = :id")
    List<SkillExperienceLevel> findDistinctBySkillId(@Param("id") String id);
    List<SkillExperienceLevel> findAllBySkillId(String skillId);
    
    @Query("select distinct sel from SkillExperienceLevel as sel where sel.level=:level and sel.skill in"
    		+ " (select ps.skill from PersonalSkill as ps where ps.personal.id=:idPersonal)")
    List<SkillExperienceLevel> findBySkillAndLevel2(Level level, String idPersonal);
    
    @Query("select distinct sel from SkillExperienceLevel as sel where sel.level=:level and sel.skill in (:skills) and sel.skill in"
    		+ " (select ps.skill from PersonalSkill as ps where ps.personal.id=:idPersonal)")
    List<SkillExperienceLevel> findBySkillAndLevelAndSkillIn(Level level, List<Skill> skills ,String idPersonal);

    @Query("select sel from SkillExperienceLevel sel where sel.name=:name and sel.skill.name=:nameSkill and sel.skillGroup.id=:skillGroupId")
    Optional<SkillExperienceLevel> findByNameAndSkillNameAndSkillGroupId2(String name, String nameSkill, String skillGroupId);
    
    @Query("select sel from SkillExperienceLevel sel where sel.name=:name and sel.skill.name=:nameSkill")
    Optional<SkillExperienceLevel> findByNameAndSkillName(String name, String nameSkill);
}
