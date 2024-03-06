package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillLevel;

public interface SkillLevelRepository extends JpaRepository<SkillLevel, String>{

	Optional<SkillLevel> findByLevelAndSkillAndSkillGroupAndLevelLable(Level level, Skill skill, SkillGroup skillGroup, String levelLable);
	
	@Query("select sl from SkillLevel sl where sl.skill.id=:skillId and sl.level.id=:levelId")
	Optional<SkillLevel> findBySkillIdAndLevelId(String skillId, String levelId);
	
	@Query("select sl from SkillLevel sl left join PersonalSkill ps on sl.skill.id=ps.skill.id where ps.personal.id=:personalId and sl.level.id=:levelId")
	List<SkillLevel> findExpectedLevel(String personalId, String levelId);

	@Query("select sl from SkillLevel sl where sl.level.id=:levelId")
	List<SkillLevel> findExpectedLevelByAssociateLevelId(String levelId);

	List<SkillLevel> findBySkillIdIn(List<String> skillIdList);
	Optional<SkillLevel> findByLevelAndSkill(Level level, Skill skill);
	
	List<SkillLevel> findBySkillId(String skillId);
}
