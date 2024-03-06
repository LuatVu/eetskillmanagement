package com.bosch.eet.skill.management.converter;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.text.ParseException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.dto.TrainingCourseDto;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentCourseDocument;
import com.bosch.eet.skill.management.entity.Course;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TrainingCourseConverter {
	
    @Autowired
    private MessageSource messageSource;
    	
    public TrainingCourseDto convertToSearchDTO(TrainingCourse trainingCourse) {
    	TrainingCourseDto trainingCourseDto = new TrainingCourseDto();
        trainingCourseDto.setId(trainingCourse.getId());
        Course course = trainingCourse.getCourse();
        if (!Objects.isNull(course)) {
        	trainingCourseDto.setCourseId(course.getId());
        	trainingCourseDto.setName(course.getName());
            trainingCourseDto.setCourseType(course.getCourseType());
            trainingCourseDto.setTrainer(trainingCourse.getTrainer());
            trainingCourseDto.setDuration(trainingCourse.getEffort());
            trainingCourseDto.setTargetAudience(trainingCourse.getTargetAudience().getName());
            if (!Objects.isNull(trainingCourse.getDate())) {
                trainingCourseDto.setDate(Constants.SIMPLE_DATE_FORMAT.format(trainingCourse.getDate()));
            }
            if (!Objects.isNull(course.getCategory())) {
            	trainingCourseDto.setCategoryName(String.valueOf(course.getCategory().getName()));
            }            trainingCourseDto.setStatus(trainingCourse.getStatus());
            trainingCourseDto.setDescription(course.getDescription());
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Course not found", null);
        }
        return trainingCourseDto;
    }
    
    public TrainingCourseDto convertToDetailDTO(TrainingCourse trainingCourse) {
    	TrainingCourseDto trainingCourseDto = new TrainingCourseDto();
        trainingCourseDto.setId(trainingCourse.getId());
        Course course = trainingCourse.getCourse();
        if (!Objects.isNull(course)) {
        	trainingCourseDto.setCourseId(course.getId());
        	trainingCourseDto.setName(course.getName());
            trainingCourseDto.setCourseType(course.getCourseType());
            trainingCourseDto.setTrainer(trainingCourse.getTrainer());
            trainingCourseDto.setDuration(trainingCourse.getEffort());
            if (!Objects.isNull(trainingCourse.getDate())) {
                trainingCourseDto.setDate(Constants.SIMPLE_DATE_FORMAT.format(trainingCourse.getDate()));
            }
            if (!Objects.isNull(course.getCategory())) {
            	trainingCourseDto.setCategoryName(String.valueOf(course.getCategory().getName()));
            }
            if (!Objects.isNull(trainingCourse.getTargetAudience())) {
                trainingCourseDto.setTargetAudience(trainingCourse.getTargetAudience().getName());
            }
            trainingCourseDto.setStatus(trainingCourse.getStatus());
            trainingCourseDto.setDescription(course.getDescription());
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Course not found", null);
        }
        return trainingCourseDto;
    }
    
    public DepartmentCourseDocument convertToDocument (TrainingCourse trainingCourse) {
    	return DepartmentCourseDocument.builder()
    			.id(trainingCourse.getId())
    			.courseId(trainingCourse.getId())
    			.courseName(trainingCourse.getCourse().getName())
    			.courseType(trainingCourse.getCourse().getCourseType())
    			.trainer(trainingCourse.getTrainer())
    			.startDate(trainingCourse.getDate())
    			.effort(trainingCourse.getEffort())
    			.targetAudience(trainingCourse.getTargetAudience().getName())
    			.status(trainingCourse.getStatus())
    			.build();
    }
    
    public DepartmentCourseDocument convertDTOToDocument (TrainingCourseDto trainingCourseDto) {
    	DepartmentCourseDocument courseDocument = DepartmentCourseDocument.builder()
			.id(trainingCourseDto.getId())
			.courseId(trainingCourseDto.getId())
			.courseName(trainingCourseDto.getName())
			.courseType(trainingCourseDto.getCourseType())
			.trainer(trainingCourseDto.getTrainer())
			.effort(trainingCourseDto.getDuration())
			.targetAudience(trainingCourseDto.getTargetAudience())
			.status(trainingCourseDto.getStatus())
			.build();
    	
    	try {
    		courseDocument.setStartDate(Constants.SIMPLE_DATE_FORMAT.parse(trainingCourseDto.getDate()));
		} catch (ParseException e) {
			log.error(e.toString());
            throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                    messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

    	return courseDocument;
    }
}
