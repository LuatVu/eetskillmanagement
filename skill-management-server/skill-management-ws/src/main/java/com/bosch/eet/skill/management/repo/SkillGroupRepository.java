package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.entity.SkillGroup;

public interface SkillGroupRepository extends JpaRepository<SkillGroup, String>, JpaSpecificationExecutor {
	@Query("select distinct new SkillGroup (sg.name) from SkillGroup sg")
	List<SkillGroup> findNameList();
	
	@Query("select distinct new SkillGroup (sg.name) "
			+ "from SkillGroup sg join SkillType st on sg.skillType.id = st.id "
			+ "where st.name = :skillType")
	List<SkillGroup> findNameByTypeList(String skillType);

	Optional<SkillGroup> findByName(String name);

	@Query("select distinct new com.bosch.eet.skill.management.dto.SkillGroupDto(sg.name," +
			" cast(count(distinct ps.personal.id) as int) as associates)" +
			" from SkillGroup sg join Skill s on s.skillGroup.id = sg.id" +
			" join PersonalSkill ps on ps.skill.id in s" +
			" join Personal p on ps.personal.id = p.id" +
			" join Team t on p.team.id = t.id" +
			" where :team is null or t.name = :team" +
			" group by sg.name" +
			" order by count(distinct ps.personal.id) desc")
	Page<SkillGroupDto> findTopSkillGroups(String team, Pageable pageable);
	
	@Query("select distinct ps.skill.skillGroup from PersonalSkill as ps where ps.skill.id in (:setMainSkill)")
	List<SkillGroup> getSkillByIdMainSkill(String setMainSkill);
	
	List<SkillGroup> findByIdIn(List<String> ids);
	
	@Query("SELECT sg.id, sg.name FROM SkillGroup sg WHERE sg.skillType.name = ?1")
    List<Object[]> findAllSkillGroupIdName(String type);

}
