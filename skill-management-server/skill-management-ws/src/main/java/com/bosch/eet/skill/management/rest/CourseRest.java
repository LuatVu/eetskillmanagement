package com.bosch.eet.skill.management.rest;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.CourseMemberDto;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.dto.TrainingCourseDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.CourseService;

import io.github.jhipster.web.util.PaginationUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DUP5HC
 */

@RestController
@Slf4j
public class CourseRest {
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
    private CourseService courseService;
    
    //  Get All Training Courses
    @GetMapping(value = Routes.URI_REST_COURSE)
    public ResponseEntity<List<TrainingCourseDto>> getCourses(@RequestParam Map<String, String> filterParams) {
        Page<TrainingCourseDto> trainingCoursesDto = courseService.findAllTrainingCourse(Pageable.unpaged(), filterParams);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), trainingCoursesDto);
        return ResponseEntity.ok().headers(headers).body(trainingCoursesDto.getContent());
    }
    
//	Get Training Course Details
    @GetMapping(value = Routes.URI_REST_COURSE_ID)
    public ResponseEntity<TrainingCourseDto> getCourseDetail(@PathVariable(name = "course_id") String courseId) {
    	TrainingCourseDto trainingCourseDto = courseService.findCourseByTrainingCourseId(courseId);
    	return ResponseEntity.ok().body(trainingCourseDto);
    }
    
//  Add training course
    @PostMapping(value = Routes.URI_REST_COURSE)
    public GenericResponseDTO<TrainingCourseDto> addNewCourse(@RequestBody TrainingCourseDto trainingCourseDto) {
        try {
            final TrainingCourseDto newCourse =
                    courseService.addNewCourse(trainingCourseDto);
            return GenericResponseDTO.<TrainingCourseDto>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .data(newCourse)
                    .timestamps(new Date())
                    .build();
        } catch (Exception e) {
            log.error(messageSource.getMessage(MessageCode.SKM_ERROR_ADDING_COURSE.toString(),
                    null, LocaleContextHolder.getLocale()));
            return GenericResponseDTO.<TrainingCourseDto>builder()
                    .code(MessageCode.SKM_ERROR_ADDING_COURSE.toString())
                    .timestamps(new Date())
                    .build();
        }
    }
    
//  Delete Course by courseID
    @PostMapping(value = Routes.URI_REST_COURSE_DELETE)
    public GenericResponseDTO<String> deleteCourse(@PathVariable(name = "course_id") String courseId) {
        boolean isCourseDeleted;
        try {
            isCourseDeleted = courseService.deleteCourse(courseId);
            if (isCourseDeleted == true) {
                return GenericResponseDTO.<String>builder()
                        .code(MessageCode.SUCCESS.toString())
                        .data("Course deleted successfully!")
                        .build();
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return GenericResponseDTO.<String>builder()
                    .code(MessageCode.SKM_COURSE_IS_BEING_ASSIGNED.toString())
                    .timestamps(new Date())
                    .build();
        }
    }
    
//	Update Course Details by courseId
    @PostMapping(value = Routes.URI_REST_COURSE_UPDATE)
    public ResponseEntity<TrainingCourseDto> updateCourse(@PathVariable(name = "course_id") String courseId,
    		@RequestBody TrainingCourseDto trainingCourseDto) {
    	final TrainingCourseDto newCourse = courseService.updateCourseDetails(trainingCourseDto, courseId);
    	return ResponseEntity.ok().body(newCourse);
    }
    
//  Add List Courses by Excel Files
    @PostMapping(value = Routes.URI_REST_COURSE_ADD_COURSE)
    public GenericResponseDTO<String> addCoursesByExcel(@RequestParam("file") MultipartFile file) {
        try {
            courseService.addTrainingCoursesFromExcel(file);
            return GenericResponseDTO.<String>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .timestamps(new Date())
                    .build();
        } catch (Exception e) {
            log.error(MessageCode.HANDLE_EXCEL_FAIL.toString());
            return GenericResponseDTO.<String>builder()
                    .code(e.getMessage())
                    .timestamps(new Date())
                    .build();
        }
    }

    @GetMapping(value = Routes.URI_REST_COURSE_MEMBERS)
    public GenericResponseDTO<List<PersonalCourseDto>> findAllMembers(@PathVariable(name = "course_id") String courseId) {
        return GenericResponseDTO.<List<PersonalCourseDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(courseService.findAllCourseMembers(courseId))
                .build();
    }
    
    @PostMapping(value = Routes.URI_REST_ASSIGN_COURSE)
    public GenericResponseDTO<String> assignCourseForPersonal(@PathVariable(name = "course_id") String courseId,
    		@RequestBody List<CourseMemberDto> listCourseMembers){
        log.info(listCourseMembers.toString());
        GenericResponseDTO<String> response = new GenericResponseDTO<>();
        try {
        	courseService.assignCourseForListPersonal(courseId, listCourseMembers);
            response.setCode(MessageCode.COURSE_ASSIGN_SUCCESS.toString());
            response.setTimestamps(new Date());
            return response;

        } catch (Exception e) {
        	log.error(e.getMessage());
            response.setCode(e.getMessage());
            response.setTimestamps(new Date());
            return response;

        }
		}

	@GetMapping(value = Routes.URI_REST_COURSE_TEMPLATE)
    public ResponseEntity<Resource> downloadTemplate() {
        InputStreamResource inputStreamResource = new InputStreamResource(courseService.downloadExcel());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.xlsx")
                .contentType(new MediaType("application", "force-download"))
                .body(inputStreamResource);
    }

}