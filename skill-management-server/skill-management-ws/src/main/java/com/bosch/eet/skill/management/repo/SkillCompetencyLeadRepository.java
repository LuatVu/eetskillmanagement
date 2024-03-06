package com.bosch.eet.skill.management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.SkillCompetencyLead;

public interface SkillCompetencyLeadRepository extends JpaRepository<SkillCompetencyLead, String>, JpaSpecificationExecutor {

    @Query("select distinct new SkillCompetencyLead (s.personal) from SkillCompetencyLead s")
    List<SkillCompetencyLead> findNameList();
	
	void deleteByPersonalId(String personalId);

	List<SkillCompetencyLead> findAllBySkillId(String skillId);
	
	List<SkillCompetencyLead> findByPersonalId(String personalId);   	
	
	List<SkillCompetencyLead> findBySkillIdOrderByPersonalAsc(String skillId);
	
	List<SkillCompetencyLead> findBySkillIdAndPersonalId(String skillId, String personalId);
	
	List<SkillCompetencyLead> findBySkillGroupIdOrderByPersonalAsc(String skillId);

	void deleteByPersonalIdAndSkillGroupId(String personalId, String skillGroupId);
	
    @Modifying
	@Query("delete from SkillCompetencyLead s where s.personal.id = :personalId and s.skill is not null")
	void deleteByPersonalIdAndWhereSkillNotNull(String personalId);
	
    @Modifying
	@Query("delete from SkillCompetencyLead s where s.skillGroup.id = :skillGroupId and s.skill is null")
	void deleteBySkillGroupIdAndWhereSkillIdIsNull(String skillGroupId);

	@Modifying
	@Query("update SkillCompetencyLead scl set scl.skill = null where scl.skill.id = :skillId")
	void updateSkillToNullBySkillId(String skillId);

	void deleteBySkillGroupId(String skillGroupId);

	@Query("select distinct new SkillCompetencyLead (s.personal, s.skillGroup) " +
			"from SkillCompetencyLead s where s.skillGroup.id = :skillGroupId")
	List<SkillCompetencyLead> findDistinctBySkillGroupId(String skillGroupId);
}
