package com.bosch.eet.skill.management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bosch.eet.skill.management.entity.SkillHighlight;

@Repository
public interface SkillsHighlightReposistory extends JpaRepository<SkillHighlight, String> {
	
	void deleteByPersonalId(String personalId);
	
	List<SkillHighlight> findByPersonalId(String personalId);
	
	@Query("select shl.personalSkill.skill.id from SkillHighlight shl where shl.personal.id=:personalId")
	List<String> findIdSkillHightlightByPersonalId(String personalId);
	
	List<SkillHighlight> findByPersonalSkillSkillId(String skillId);
}
