/**
 * 
 */
package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.SkillTagDto;
import com.bosch.eet.skill.management.entity.SkillTag;

/**
 * @author VOU6HC
 */
public interface SkillTagRepository extends JpaRepository<SkillTag, String>{
	
	Optional<SkillTag> findByName(String name);
	
	List<SkillTag> findByNameIn(List<String> name);

	Optional<SkillTag> findByNameIgnoreCase(String name);
	
	List<SkillTag> findAllByOrderByOrder();

	@Query("select new com.bosch.eet.skill.management.dto.SkillTagDto( t.id, t.name, t.order as order, t.isMandatory, "
			+ "cast((select count(*) from ProjectSkillTag pst "
			+ "join Project p on pst.project.id = p.id "
			+ "join ProjectType pt on p.projectType.id = pt.id "
			+ "where pst.skillTag.id = t.id "
			+ "and pt.name = :projectType) as int) as projectCount) "
			+ "from SkillTag t "
			+ "ORDER BY t.isMandatory desc,projectCount desc,t.name asc")
	List<SkillTagDto> findAllDetailDtoCountProjectByType(String projectType);

}
