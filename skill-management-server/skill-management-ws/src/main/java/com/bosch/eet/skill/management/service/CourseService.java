package com.bosch.eet.skill.management.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.CourseMemberDto;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.dto.TrainingCourseDto;
import com.bosch.eet.skill.management.entity.TrainingCourse;

public interface CourseService {

	Page<TrainingCourseDto> findAllTrainingCourse(Pageable pageable, Map<String, String> q);

	TrainingCourseDto findCourseByTrainingCourseId(String courseId);

	TrainingCourseDto addNewCourse(TrainingCourseDto trainingCourseDto);

	TrainingCourseDto updateCourseDetails(TrainingCourseDto trainingCourseDto, String trainingCourseId);

	String addTrainingCoursesFromExcel(MultipartFile file);

	void assignCourseForListPersonal(String courseId, List<CourseMemberDto> listCourseMembers);

	List<PersonalCourseDto> findAllCourseMembers(String trainingCourseId);

	ByteArrayInputStream downloadExcel();

	boolean deleteCourse(String id);

	TrainingCourse saveAndSyncElasticTrainingCourse(TrainingCourse trainingCourse);

}
