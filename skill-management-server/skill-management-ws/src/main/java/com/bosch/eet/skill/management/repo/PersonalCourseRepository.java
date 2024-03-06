package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.PersonalCourse;

public interface PersonalCourseRepository extends JpaRepository<PersonalCourse, String>, JpaSpecificationExecutor {
	Optional<PersonalCourse> findByPersonalIdAndCourseId(String ids, String courseId);

	List<PersonalCourse> findByCourseId(String courseId);
	
	List<PersonalCourse> findByPersonalId(String personalId);
}
