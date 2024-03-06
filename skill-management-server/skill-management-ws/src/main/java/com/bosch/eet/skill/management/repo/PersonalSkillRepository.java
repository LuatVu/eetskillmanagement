package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.Skill;

public interface PersonalSkillRepository extends JpaRepository<PersonalSkill, String>, JpaSpecificationExecutor {

	List<PersonalSkill> findByPersonalId(String personalId);
	List<PersonalSkill> findBySkillId(String skillId);

	Optional<PersonalSkill> findByPersonalIdAndSkillId(String personalId, String skillId);
	
    @Query("select ps from PersonalSkill as ps where ps.personal.id =:idPersonal")
    List<PersonalSkill> findSkillsHighlight(String idPersonal);
    
    List<PersonalSkill> findByPersonalIdAndSkillIdIn(String personalId,List<String> personalSkillIds);

    @Query("select count(ps) from PersonalSkill as ps where ps.personal.id =:personalId")
    long getCountByPersonalId(String personalId);
    
    @Query("select ps.skill from PersonalSkill as ps where ps.personal.id =:personalId and ps.level !=:level")
    List<Skill> findSkillByPesonalIdAndNotEqualLevel(String personalId, float level);
}
