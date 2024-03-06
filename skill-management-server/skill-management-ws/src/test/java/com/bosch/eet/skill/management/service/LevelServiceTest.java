package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.converter.utils.LevelConverterUtil;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.service.impl.LevelServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LevelServiceTest {

	@InjectMocks
	private LevelServiceImpl levelService;

	@Mock
	private LevelRepository levelRepository;

	@Mock
	private LevelConverterUtil levelConverter;

	// find
	@Test
	@DisplayName("Find all levels")
	@Transactional
	void getLevel_returnListOfLevels() {
		when(levelRepository.findAll()).thenReturn(Arrays.asList(Level.builder().id("1").name("a").build()));

		List<String> actualResult = levelService.findAllLevels();
		System.out.println(actualResult);
		assertThat(actualResult).isNotEmpty();
	}

	// find null
	@Test
	@DisplayName("Find empty level")
	void getLevel_returnListOfEmptyLevel() {
		when(levelRepository.findAll()).thenReturn(new ArrayList<>());
		List<String> skills = levelService.findAllLevels();
		assertThat(skills).isEmpty();

	}

	@Test
	@DisplayName("Get level and return list level DTO")
	void getLevel_returnListLevelDTO() {
		List<Level> levels = new ArrayList<>();
		levels.add(Level.builder().id("1").name("a").build());
		levels.add(Level.builder().id("2").name("b").build());
		levels.add(Level.builder().id("3").name("c").build());
		levels.add(Level.builder().id("4").name("d").build());
		when(levelRepository.findByOrderByName()).thenReturn(levels);
		assertThat(levelService.findAll()).hasSize(4);
	}
}
