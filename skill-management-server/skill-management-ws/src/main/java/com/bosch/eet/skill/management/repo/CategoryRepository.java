package com.bosch.eet.skill.management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, String> {

	Optional<Category> findById(String id);

	Optional<Category> findByName(String name);
}
