package com.bosch.eet.skill.management.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.entity.PersonalSkillGroup;

public interface PersonalSkillGroupRepository extends JpaRepository<PersonalSkillGroup, String>{
    void deleteByPersonalId(String personalId);
    List<PersonalSkillGroup> findBySkillGroupId(String skillGroupId);
}
