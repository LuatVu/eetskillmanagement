/**
 * @author VOU6HC
 */
package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.SkillType;


public interface SkillTypeRepository extends JpaRepository<SkillType, String>, JpaSpecificationExecutor {
	Optional<SkillType> findByName (String name);

}
