package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.ExcelFileHandler;
import com.bosch.eet.skill.management.converter.TrainingCourseConverter;
import com.bosch.eet.skill.management.converter.utils.PersonalCourseConverterUtil;
import com.bosch.eet.skill.management.dto.CourseMemberDto;
import com.bosch.eet.skill.management.dto.PersonalCourseDto;
import com.bosch.eet.skill.management.dto.TrainingCourseDto;
import com.bosch.eet.skill.management.dto.excel.XLSXCoursesDto;
import com.bosch.eet.skill.management.elasticsearch.document.DepartmentCourseDocument;
import com.bosch.eet.skill.management.elasticsearch.repo.CourseElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.CourseSpringElasticRepository;
import com.bosch.eet.skill.management.entity.Category;
import com.bosch.eet.skill.management.entity.Course;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalCourse;
import com.bosch.eet.skill.management.entity.TrainingCourse;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.repo.CategoryRepository;
import com.bosch.eet.skill.management.repo.CourseRepository;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalCourseRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.TrainingCourseRepository;
import com.bosch.eet.skill.management.service.CourseService;
import com.bosch.eet.skill.management.service.PersonalService;
import com.bosch.eet.skill.management.specification.TrainingCourseSpecification;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseElasticRepository courseElasticRepository;

    private final CourseSpringElasticRepository courseSpringElasticRepo;

    private final PersonalCourseRepository personalCourseRepository;
	
    private final TrainingCourseRepository trainingCourseRepository;
    
    private final PersonalRepository personalRepository;
    
    private final CourseRepository courseRepository;
    
    private final LevelRepository levelRepository;
    
    private final CategoryRepository categoryRepository;
    
    private final MessageSource messageSource;
    
    private final TrainingCourseConverter trainingCourseConverter;    
    
    private final PersonalService personalService;

    private final Map<String, String> emailList = new HashMap<>();
    
    @Autowired
    private EmailService emailService;
    
//    find all training course
    @Override
    public Page<TrainingCourseDto> findAllTrainingCourse(Pageable pageable, Map<String,String> q) {
        Specification<TrainingCourse> specification = TrainingCourseSpecification.search(q);
        Page<TrainingCourse> trainingCourses = trainingCourseRepository.findAll(specification, pageable);
        return trainingCourses.map(trainingCourseConverter::convertToSearchDTO);
    }
//    view training course detail
    @Override
    public TrainingCourseDto findCourseByTrainingCourseId(String courseId) {
        HashMap<String,String> hmSearch = new HashMap<>();
        hmSearch.put("training_course_id", courseId);
        Specification<TrainingCourse> specification = TrainingCourseSpecification.search(hmSearch);
        Page<TrainingCourse> trainingCourses = trainingCourseRepository.findAll(specification, Pageable.unpaged());
        if(!trainingCourses.getContent().isEmpty()){
            TrainingCourse trainingCourse = trainingCourses.getContent().get(0);
            return trainingCourseConverter.convertToDetailDTO(trainingCourse);
        } else {
            throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the course", null);
        }
    }
    
//  add training course
    @Transactional
    @Override
    public TrainingCourseDto addNewCourse(TrainingCourseDto trainingCourseDto) {
        Optional<Course> courseOpt = courseRepository
                .findByName(trainingCourseDto.getName());
        if (courseOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_COURSE_ALREADY_EXIST.toString(),
                    messageSource.getMessage(MessageCode.SKM_COURSE_ALREADY_EXIST.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
        Course course = new Course();
        course.setName(trainingCourseDto.getName());
        course.setDescription(trainingCourseDto.getDescription());
        course.setCourseType(trainingCourseDto.getCourseType());
        Optional<Category> categoryOpt = categoryRepository
                .findById(trainingCourseDto.getCategory());
        if (categoryOpt.isPresent()) {
            course.setCategory(categoryOpt.get());
        }
        final Course newCourse = courseRepository.save(course);

        TrainingCourse newTrainingCourse = new TrainingCourse();
        newTrainingCourse.setId(trainingCourseDto.getId());
        newTrainingCourse.setCourse(newCourse);
        newTrainingCourse.setEffort(trainingCourseDto.getDuration());
        newTrainingCourse.setTrainer(trainingCourseDto.getTrainer());        
        Date newDate;
        try {
			newDate = Constants.SIMPLE_DATE_FORMAT.parse(trainingCourseDto.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new SkillManagementException(MessageCode.INVALID_DATE.toString(),
                    messageSource.getMessage(MessageCode.INVALID_DATE.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
		}
        
        newTrainingCourse.setDate(newDate);
        newTrainingCourse.setStatus(trainingCourseDto.getStatus());
        newTrainingCourse.setTargetAudience(levelRepository.getById(trainingCourseDto.getTargetAudience()));
        final TrainingCourse savedCourse = this.saveAndSyncElasticTrainingCourse(newTrainingCourse);
        
        return trainingCourseConverter.convertToDetailDTO(savedCourse);
    }

//  update course details
    @Override
    @Transactional
    public TrainingCourseDto updateCourseDetails(TrainingCourseDto trainingCourseDto, @NonNull String trainingCourseId) {
        Optional<TrainingCourse> trainingCourseEntity = trainingCourseRepository.findById(trainingCourseId);
        if (trainingCourseEntity.isPresent()) {
            TrainingCourse trainingCourse = trainingCourseEntity.get();
            trainingCourse.setEffort(trainingCourseDto.getDuration());
            trainingCourse.setTrainer(trainingCourseDto.getTrainer());        
            Date newDate;
            try {
    			newDate = Constants.SIMPLE_DATE_FORMAT.parse(trainingCourseDto.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Wrong date format", null);
    		}
            trainingCourse.setDate(newDate);
            trainingCourse.setStatus(trainingCourseDto.getStatus());

            // Target Audience
            Level level = levelRepository.findById(trainingCourseDto.getTargetAudience()).orElse(null);
            if (level == null) {
                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the Target Audience", null);
            }
            trainingCourse.setTargetAudience(level);
            TrainingCourse updatedCourse = saveAndSyncElasticTrainingCourse(trainingCourse);
            return TrainingCourseDto.builder()
                    .id(updatedCourse.getId())
                    .build();
        } else {
        	throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Cannot find the course", null); 
        }
    }
	//  import course from file excel
    @Override
    @Transactional
    public String addTrainingCoursesFromExcel(MultipartFile file) {
    	try {
    		List<XLSXCoursesDto> xlsxCourseDtos = ExcelFileHandler.convertXLSXFileToTrainingCourse(file);
    		Level targetAudience = levelRepository.findLevelByName("L50").orElseThrow(
                    () -> new SkillManagementException(
                            MessageCode.DEFAULT_LEVEL_NOT_EXIST.toString(),
                            messageSource.getMessage(
                                    MessageCode.DEFAULT_LEVEL_NOT_EXIST.toString(),
                                    null,
                                    LocaleContextHolder.getLocale()
                            ),
                            null
                    )
    		);

            Set<Course> existedCourse = new HashSet<>();
            Set<TrainingCourse> allTrainingCourseToSave = new HashSet<>();
            HashMap<String, Course> allDbCourse = new HashMap<>();
            for(Course course: courseRepository.findAll()){allDbCourse.put(course.getName().toUpperCase(), course);}
    		
    		xlsxCourseDtos.forEach(
    			xlsxCoursesDto ->{
    				Course course = xlsxCoursesDto.getCourse();
//    				Optional<Course> courseOpt = courseRepository.findByName(course.getName());

                    Course courseInDb = allDbCourse.get(course.getName().toUpperCase());
    				if (courseInDb != null) {

//                        Get ID from the excel does not make sense cause this id will not be set on db
//    					courseInDb.setId(course.getId());
    					try {
    						courseInDb.setName(course.getName());
    					}catch (Exception e) {
    		                e.printStackTrace();
    		                throw new EETResponseException(String.valueOf(NOT_FOUND.value()), "Wrong date format", null);
    		    		}
    					courseInDb.setCategory(course.getCategory());
    					courseInDb.setCourseType(course.getCourseType());
    					courseInDb.setDescription(xlsxCoursesDto.getDesription());
                        existedCourse.add(courseInDb);

    					TrainingCourse trainingCourse = trainingCourseRepository.findById(courseInDb.getId()).orElse(
    							TrainingCourse.builder()
    							.id(courseInDb.getId())
    							.course(courseInDb)
    							.build()
    					);
    					trainingCourse.setTargetAudience(levelRepository.findLevelById(xlsxCoursesDto.getTargetAudience()).orElse(targetAudience));
    		            Date newDate;
    		            try {
    		    			newDate = Constants.SIMPLE_DATE_FORMAT.parse(xlsxCoursesDto.getDate());
    		            } catch (ParseException e) {
    		                e.printStackTrace();
    		                throw new SkillManagementException(MessageCode.SKM_COURSE_DUPLICATE_NAME.toString(),
    		                        messageSource.getMessage(MessageCode.SKM_COURSE_DUPLICATE_NAME.toString(), null,
    		                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
    		    		}
    					trainingCourse.setDate(newDate);
    					trainingCourse.setEffort(xlsxCoursesDto.getDuration());
    					trainingCourse.setTrainer(xlsxCoursesDto.getTrainer());
    					trainingCourse.setStatus(xlsxCoursesDto.getStatus());

                        allTrainingCourseToSave.add(trainingCourse);

    				} else {
    					course.setId(null);
//    					Course newCourse = courseRepository.save(course);
//    					String courseId = newCourse.getId();
    					Optional<Category> newCat = categoryRepository.findByName(xlsxCoursesDto.getCategory());
    					course.setCategory(newCat.get());
    					course.setCourseType(xlsxCoursesDto.getType());
    					courseRepository.save(course);
    					TrainingCourse trainingCourse = TrainingCourse.builder()
//    							.id(course.getId())
    							.course(course)
    							.effort(xlsxCoursesDto.getDuration())
    							.trainer(xlsxCoursesDto.getTrainer())
    							.targetAudience(levelRepository.findLevelById(xlsxCoursesDto.getTargetAudience()).orElse(targetAudience))
    							.status(xlsxCoursesDto.getStatus())
    							.build();

                        allTrainingCourseToSave.add(trainingCourse);
                    }
    			}
    		);

            // Update existed course
            courseRepository.saveAll(existedCourse);
            // Save/Update training courses
            trainingCourseRepository.saveAll(allTrainingCourseToSave);
            // Sync training courses to elasticSearch
            courseSpringElasticRepo.saveAll(allTrainingCourseToSave
                    .stream().map(trainingCourseConverter::convertToDocument)
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new UserManagementBusinessException(
                    MessageCode.HANDLE_EXCEL_FAIL.toString(),
                    messageSource.getMessage(
                            MessageCode.HANDLE_EXCEL_FAIL.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }
        return MessageCode.SUCCESS.toString();
    }
    
    @Override
    @Transactional
    public void assignCourseForListPersonal (String courseId, List<CourseMemberDto> listCourseMembers) {
        for (CourseMemberDto courseMemberDto : listCourseMembers) {
        	boolean isRegistered = false;
        	List<PersonalCourse> listCourse = personalCourseRepository.findByPersonalId(courseMemberDto.getId());
        	for(PersonalCourse personalCourse : listCourse) {
    			log.info(courseId);
    			log.info(personalCourse.getId());
        		if (personalCourse.getCourse().getId().equalsIgnoreCase(courseId)) {
                    log.info(messageSource.getMessage(MessageCode.SKM_COURSE_ALREADY_EXIST.toString(), null,
                            LocaleContextHolder.getLocale()));
                    isRegistered = true;
        		}
        	}
        	if (!isRegistered) {
	            personalService.assignCourseForPersonal(courseId, courseMemberDto);
	        	try {
	            	Optional<Personal> personOpt = personalRepository.findById(courseMemberDto.getId());
	            	Personal personal = personOpt.get();
	            	emailList.put(personal.getUser().getName(), personal.getUser().getEmail());
	            	
	            	String courseName = courseRepository.getById(courseId).getName();
	            	
	    			emailList.forEach((key, value) -> {
	    				emailService.mailToUserAssignedCourse(key, value, courseName, Constants.WAM_SERVER);
	    			});
	        	} catch (Exception e) {
	        		
	        	}
        	}
        }
    }

    @Override
    public List<PersonalCourseDto> findAllCourseMembers(String trainingCourseId) {
        List<PersonalCourse> personalCourses = personalCourseRepository
                .findByCourseId(trainingCourseId);
        return PersonalCourseConverterUtil.convertToMemberDTOS(personalCourses);
    }

    @Override
    public ByteArrayInputStream downloadExcel() {
        try {
            // Create the new Excel file and the byte array output stream for
            // writing the data into the Excel file.
            Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // The headers (column names) of the Excel file.
            String[] headers = {"course_id", "course_name", "trainer", "date",
                "course_category", "duration", "type", "target_audience",
                "description", "status"};
            // Create a new Excel sheet.
            Sheet sheet = workbook.createSheet();
            // Create the index row for the headers.
            Row rowHeader = sheet.createRow(0);
            // Create the cell and set the cell value matching the headers.
            for (int col = 0; col < headers.length; col++) {
                Cell cell = rowHeader.createCell(col);
                cell.setCellValue(headers[col]);
            }
            // Create the dummy data for writing to the Excel file.
            // The first row
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("'5");
            row1.createCell(1).setCellValue("testCourse5");
            row1.createCell(2).setCellValue("trainer5");
            row1.createCell(3).setCellValue("'2022-15-12");
            row1.createCell(4).setCellValue("test1");
            row1.createCell(5).setCellValue(1);
            row1.createCell(6).setCellValue("R&D");
            row1.createCell(7).setCellValue("L50");
            row1.createCell(8).setCellValue("test Course 5");
            row1.createCell(9).setCellValue("NEW");
            // The second row
            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("'6");
            row2.createCell(1).setCellValue("testCourse6");
            row2.createCell(2).setCellValue("trainer6");
            row2.createCell(3).setCellValue("'2022-16-12");
            row2.createCell(4).setCellValue("test2");
            row2.createCell(5).setCellValue(2);
            row2.createCell(6).setCellValue("R&D");
            row2.createCell(7).setCellValue("L50");
            row2.createCell(8).setCellValue("test Course 6");
            row2.createCell(9).setCellValue("ON-GOING");
            // Write the data (including the headers and the dummy data) into
            // the Excel file.
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new SkillManagementException(MessageCode.SKM_COURSE_TEMPLATE_DOWNLOAD_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SKM_COURSE_TEMPLATE_DOWNLOAD_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean deleteCourse(String id) {
        TrainingCourseDto trainingCourseDto
                = findCourseByTrainingCourseId(id);

        if (Objects.isNull(trainingCourseDto)) {
            throw new SkillManagementException(MessageCode.SKM_COURSE_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_COURSE_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        try {
            trainingCourseRepository.deleteById(id);
            courseElasticRepository.removeById(id);
        } catch (Exception e) {
            log.error(e.toString());
            throw new SkillManagementException(MessageCode.SKM_COURSE_DELETE_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SKM_COURSE_DELETE_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null);
        }

        return true;
    }

    @Override
    public TrainingCourse saveAndSyncElasticTrainingCourse(TrainingCourse trainingCourse){
        TrainingCourse savedTrainingCourse = trainingCourseRepository.save(trainingCourse);

        // index to elastic search
        if (courseElasticRepository.existById(trainingCourse.getId())) {
            courseElasticRepository.update(trainingCourse);
        } else {
            DepartmentCourseDocument document = trainingCourseConverter.convertToDocument(trainingCourse);
            courseElasticRepository.insert(document);
        }
        return savedTrainingCourse;
    }
}

