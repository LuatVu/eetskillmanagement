package com.bosch.eet.skill.management.converter.utils;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalCourseDocument;
import com.bosch.eet.skill.management.entity.PersonalCourse;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import com.bosch.eet.skill.management.exception.EETResponseException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public final class PersonalCourseConverterUtil {

	private PersonalCourseConverterUtil() {
		// prevent instantiation
	}
	
    public static PersonalCourseDto convertToSearchDTO(PersonalCourse personalCourse) {
        PersonalCourseDto personalCourseDto = new PersonalCourseDto();
        personalCourseDto.setId(personalCourse.getId());
        TrainingCourse trainingCourse = personalCourse.getCourse();
        if (!Objects.isNull(trainingCourse)) {
            personalCourseDto.setPersonalCourseName(trainingCourse.getCourse().getName());
            personalCourseDto.setCourseId(trainingCourse.getId());
            personalCourseDto.setCourseType(trainingCourse.getCourse().getCourseType());
            if (!Objects.isNull(personalCourse.getStartDate())) {
                personalCourseDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(personalCourse.getStartDate()));
            }
            if (!Objects.isNull(personalCourse.getEndDate())) {
                personalCourseDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(personalCourse.getEndDate()));
            }
            personalCourseDto.setStatus(personalCourse.getStatus());
            personalCourseDto.setCertificate(personalCourse.getCertificate());
            personalCourseDto.setTrainer(personalCourse.getTrainer());
            personalCourseDto.setDuration(personalCourse.getDuration());
            if (!Objects.isNull(trainingCourse.getCourse().getCategory())) {
            	personalCourseDto.setCategoryName(String.valueOf(trainingCourse.getCourse().getCategory().getName()));
            }

        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Course not found", null);
        }
        return personalCourseDto;
    }
    
    public static PersonalCourseDto convertToDetailDTO(PersonalCourse personalCourse) {
    	PersonalCourseDto personalCourseDto = new PersonalCourseDto();
        personalCourseDto.setId(personalCourse.getId());
        TrainingCourse trainingCourse = personalCourse.getCourse();
        if (!Objects.isNull(trainingCourse)) {
            personalCourseDto.setCourseId(personalCourse.getId());
            personalCourseDto.setPersonalCourseName(trainingCourse.getCourse().getName());
            personalCourseDto.setCourseType(personalCourse.getCourseType());
            if (!Objects.isNull(personalCourse.getStartDate())) {
                personalCourseDto.setStartDate(Constants.SIMPLE_DATE_FORMAT.format(personalCourse.getStartDate()));
            }
            if (!Objects.isNull(personalCourse.getEndDate())) {
                personalCourseDto.setEndDate(Constants.SIMPLE_DATE_FORMAT.format(personalCourse.getEndDate()));
            }
            personalCourseDto.setStatus(personalCourse.getStatus());
            personalCourseDto.setCertificate(personalCourse.getCertificate());
            personalCourseDto.setTrainer(personalCourse.getTrainer());
            personalCourseDto.setDuration(personalCourse.getDuration());
            if (!Objects.isNull(trainingCourse.getCourse().getCategory())) {
            	personalCourseDto.setCategoryName(String.valueOf(trainingCourse.getCourse().getCategory().getName()));
            }
        	
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Course not found", null);
        }

        return personalCourseDto;
    }

    public static PersonalCourseDto convertToMemberDTO(PersonalCourse personalCourse) {
        return PersonalCourseDto.builder()
        		.id(personalCourse.getId())
                .displayName(personalCourse.getPersonal().getUser().getDisplayName())
                .personalId(personalCourse.getPersonal().getId())
                .startDate(Constants.SIMPLE_DATE_FORMAT.format(personalCourse.getStartDate()))
                .endDate(Constants.SIMPLE_DATE_FORMAT.format(personalCourse.getEndDate()))
                .build();
    }

    public static List<PersonalCourseDto> convertToMemberDTOS(List<PersonalCourse> personalCourses) {
        List<PersonalCourseDto> personalCourseDtos = new ArrayList<>();
        for (PersonalCourse personalCourse : personalCourses) {
            personalCourseDtos.add(convertToMemberDTO(personalCourse));
        }
        return personalCourseDtos;
    }
    
    public static PersonalCourseDocument convertToDocument(PersonalCourse personalCourse) {
    	return PersonalCourseDocument.builder()
    			.id(personalCourse.getId())
    			.courseName(personalCourse.getCourse().getCourse().getName())
    			.status(personalCourse.getStatus())
    			.certificate(personalCourse.getCertificate())
    			.duration(personalCourse.getDuration())
    			.build();
    }
    
    public static List<PersonalCourseDocument> convertToListDocument(List<PersonalCourse> personalCourses){
    	List<PersonalCourseDocument> listPersonalCourseDocuments = new ArrayList<>();
    	for (PersonalCourse personalCourse : personalCourses) {
    		listPersonalCourseDocuments.add(convertToDocument(personalCourse));
    	}
    	return listPersonalCourseDocuments;
    }

}
