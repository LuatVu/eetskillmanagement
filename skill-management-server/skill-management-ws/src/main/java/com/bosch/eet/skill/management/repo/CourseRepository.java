package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bosch.eet.skill.management.entity.Course;

public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor {

    Optional<Course> findByName(String name);

}
