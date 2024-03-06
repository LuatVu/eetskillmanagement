package com.bosch.eet.skill.management.common;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.dto.excel.XLSXCoursesDto;
import com.bosch.eet.skill.management.dto.excel.XLSXPersonalSkillClusterDTO;
import com.bosch.eet.skill.management.dto.excel.XLSXPersonalSkillDTO;
import com.bosch.eet.skill.management.dto.excel.XLSXProjectDto;
import com.bosch.eet.skill.management.dto.excel.XLSXUsersDTO;
import com.bosch.eet.skill.management.entity.Course;
import com.bosch.eet.skill.management.usermanagement.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelFileHandler {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final Set<String> defaultProjectStatus = new HashSet<>(Arrays.asList("New", "On-going", "Done", "On-hold", "Closed"));
	private static final String defaultBlankStatus = "On-going";
    
    public static List<XLSXUsersDTO> convertXLSXFileToPersonal(MultipartFile file, String createdBy) throws IOException {

    	
        List<XLSXUsersDTO> xlsxUsersDTOS = new ArrayList<>();

        //handle excel file
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        XSSFSheet firstSheet = workbook.getSheetAt(0);

        for (Row row : firstSheet) {
            //row 0 = labels
            if (row.getRowNum() == 0) continue;

            Integer personalNumber = null;
            if (!Objects.isNull(row.getCell(0))) personalNumber = (int) row.getCell(0).getNumericCellValue();

			String userId;
            if (!Objects.isNull(row.getCell(2))) userId = row.getCell(2).getStringCellValue();
            else continue;
			if(userId.equalsIgnoreCase("")) continue;

			String fullName = null;
            if (!Objects.isNull(row.getCell(3))) fullName = row.getCell(3).getStringCellValue();

            String gender = "NA";
            if(!Objects.isNull(row.getCell(5))) gender = row.getCell(5).getStringCellValue();
			int expAtBosch = 0;
            LocalDate joinDate = null;
            if (!Objects.isNull(row.getCell(7))) {
                DataFormatter dataFormatter = new DataFormatter();
                String date = dataFormatter.formatCellValue(row.getCell(7));
                //calculate exp at bosch
                if (!date.isEmpty()) {
                    joinDate = LocalDate.parse(
                            date,
                            DateTimeFormatter.ofPattern("d-MMM-yy")
                    );

                    expAtBosch = LocalDate.now().getYear() - joinDate.getYear();
                }
            }

			Integer nonBoschExp = 0;
			if (!Objects.isNull(row.getCell(8))) nonBoschExp = (int) row.getCell(8).getNumericCellValue();

            String team = null;
			if(!Objects.isNull(row.getCell(9))) {
				team = row.getCell(9).getStringCellValue();
			}

			String grade = "L50";
            if (!Objects.isNull(row.getCell(10))) grade = row.getCell(10).getStringCellValue();

			String title = "Developer";
            if (!Objects.isNull(row.getCell(11))) title = row.getCell(11).getStringCellValue();

			String location = "Ho Chi Minh city";
            if(!Objects.isNull(row.getCell(12))) location = row.getCell(12).getStringCellValue();

			Boolean isUpdated = false;

            XLSXUsersDTO xlsxUsersDTO = XLSXUsersDTO.builder()
                    .user(
                            User.builder()
                                    .name(userId)
                                    .displayName(fullName)
                                    .status(Status.ACTIVE.getLabel())
                                    .modifiedDate(LocalDateTime.now())
                                    .modifiedBy(createdBy)
                                    .type(UserType.PERSON.getLabel())
                                    .createdBy(createdBy)
                                    .createdDate(LocalDateTime.now())
                                    .build()
                    )
					.nonBoschExp(nonBoschExp)
                    .grade(grade)
                    .personalNumber(Integer.toString(personalNumber))
                    .experienceAtBosch(expAtBosch)
                    .title(title)
                    .team(team)
                    .location(location)
                    .joinDate(joinDate)
                    .gender(Gender.getGenderByValue(gender))
                    .isUpdated(isUpdated)
                    .build();

            xlsxUsersDTOS.add(xlsxUsersDTO);
        }

        return xlsxUsersDTOS;
    }
    public static List<XLSXCoursesDto> convertXLSXFileToTrainingCourse(MultipartFile file) throws IOException {

        List<XLSXCoursesDto> xlsxCoursesDto = new ArrayList<>();

        //handle excel file

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        XSSFSheet firstSheet = workbook.getSheetAt(0);
        for (Row row : firstSheet) {
            //row 0 = labels
            if (row.getRowNum() == 0) continue;
            
            String courseId = null;
            if (!Objects.isNull(row.getCell(0))) courseId = row.getCell(0).getStringCellValue();

            String courseName = null;
            if (!Objects.isNull(row.getCell(1))) courseName = row.getCell(1).getStringCellValue();

            String trainer = null;
            if (!Objects.isNull(row.getCell(2))) trainer = row.getCell(2).getStringCellValue();

            String date = null;
            if (!Objects.isNull(row.getCell(3))) date = row.getCell(3).getStringCellValue();

            String courseCategory = null;
            if (!Objects.isNull(row.getCell(4))) courseCategory = row.getCell(4).getStringCellValue();

            Integer duration = null;
            if (!Objects.isNull(row.getCell(5))) duration = (int) row.getCell(5).getNumericCellValue();

            String type = null;
            if (!Objects.isNull(row.getCell(6))) type = row.getCell(6).getStringCellValue();

            String targetAudience = null;
            if (!Objects.isNull(row.getCell(7))) targetAudience = row.getCell(7).getStringCellValue();

            String description = null;
            if (!Objects.isNull(row.getCell(8))) description = row.getCell(8).getStringCellValue();
            
            String status = null;
            if (!Objects.isNull(row.getCell(9))) status = row.getCell(9).getStringCellValue();


            XLSXCoursesDto xlsxCoursesDTO = XLSXCoursesDto.builder()
                    .course(
                            Course.builder()
                                    .id(courseId)
                                    .name(courseName)                                    
                                    .build()
                    )
                    .trainer(trainer)
                    .date(date)
                    .category(courseCategory)
                    .duration(duration)
                    .type(type)
                    .targetAudience(targetAudience)
                    .desription(description)
                    .status(status)
                    .build();

            xlsxCoursesDto.add(xlsxCoursesDTO);

        }
        return xlsxCoursesDto;
    }

    public static List<XLSXPersonalSkillDTO> convertXLSXFileToPersonalSkill(MultipartFile file, String createdBy) throws IOException {
    List<XLSXPersonalSkillDTO> personalSkills = new ArrayList<>();
		    XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		
		    XSSFSheet firstSheet = workbook.getSheetAt(0);
		
		    for (Row row : firstSheet) {
    
            if(row.getRowNum() == 0) continue;

            //get data from excel
            if(Objects.isNull(row.getCell(0))) continue;
            String personalId = row.getCell(0).getStringCellValue();

            if(Objects.isNull(row.getCell(1))) continue;
            String skillName = row.getCell(1).getStringCellValue();

            String level = Objects.isNull(row.getCell(2))? "Level 0" : row.getCell(2).getStringCellValue();

            //handle data
            String[] split = personalId.split("-");
            personalId = split[1];

            personalSkills.add(
                    XLSXPersonalSkillDTO.builder()
                            .personalId(personalId)
                            .level(level.substring(6,7))
                            .skillName(skillName)
                            .build()
            );
        }

        return personalSkills;
    }
    
    public static List<XLSXPersonalSkillClusterDTO> convertXLSXFileToPersonalSkillCluster(MultipartFile file, String createdBy) throws IOException{
    	try(XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
    		List<XLSXPersonalSkillClusterDTO> personalSkillClusters = new ArrayList<>();
        	
        	XSSFSheet firstSheet = workbook.getSheetAt(0);
        	
        	for (Row row : firstSheet) {
        		List<String> skillClusters = new ArrayList<>();
        		if(row.getRowNum() < 2) continue;
        		
        		if(Objects.isNull(row.getCell(1))) continue;
        		String personalId = row.getCell(1).getStringCellValue();
        		int skillClusterIndex = 3;
        		
        		while (firstSheet.getRow(1).getCell(skillClusterIndex) != null) {
        			if(!Objects.isNull(row.getCell(skillClusterIndex))
        					&& StringUtils.isNotBlank(row.getCell(skillClusterIndex).getStringCellValue()))
            			skillClusters.add(row.getCell(skillClusterIndex).getStringCellValue().trim());
        			skillClusterIndex+=2;
        		}
        		
        		//hanlde data
        		String[] split = personalId.split("-");
        		if (null != split && split.length > 1) {
        			personalId = split[1];
            		
            		personalSkillClusters.add(                    
            				XLSXPersonalSkillClusterDTO.builder()
                            .personalId(personalId)
                            .skillCluster(skillClusters)
                            .build()
            				);	
        		}
        	}
        	return personalSkillClusters;
    	} catch(Exception e) {
    		log.error(e.getMessage());
    		return Collections.emptyList();
    	}
    	
    }
    public static List<XLSXProjectDto> convertXLSXFileToProjectDto(String ntid, MultipartFile file) throws IOException{
		Map<String, String> defaultProjectStatusMap = new HashMap<>();
		defaultProjectStatus.forEach(status -> defaultProjectStatusMap.put(status.toLowerCase(), status));
		
    	List<XLSXProjectDto> xlsxProjectDtos =new ArrayList<>();
    	XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
    	XSSFSheet firstSheet = workbook.getSheetAt(0);
    	for (Row row : firstSheet) {
    		
            if(row.getRowNum()==0) continue;
    		
			String projectName = null;
			Cell cellProjectName = row.getCell(0);
			
			//This condition is used to stop reading data from the file when the project name cell is null
            if(cellProjectName==null ||
                    StringUtils.isEmpty(cellProjectName.getStringCellValue()) )
                    break;
            
			projectName = extractStringCellValue(projectName, cellProjectName);

			String projectDescription = null;
			Cell cellProjectDesciption = row.getCell(1);
			projectDescription = extractStringCellValue(projectDescription, cellProjectDesciption);

			String status = defaultBlankStatus;
			Cell cellProjectStatus = row.getCell(2);
			if(!Objects.isNull(cellProjectStatus)) {
				String tempStatus = defaultProjectStatusMap.get(cellProjectStatus.getStringCellValue().toLowerCase());
				if(!Objects.isNull(tempStatus)) status = tempStatus;
			}

			String vModelPhasesStr=null;
			Cell cellProjectPhase = row.getCell(3);
			vModelPhasesStr = extractStringCellValue(vModelPhasesStr, cellProjectPhase);

			String challenge=null;
			Cell cellProjectChallenge = row.getCell(4);
			challenge = extractStringCellValue(challenge, cellProjectChallenge);

			Date startDate = null;
			Cell cellProjectStartDate = row.getCell(5);
			startDate = extractDateCellValue(startDate, cellProjectStartDate);

			Date endDate = null;
			Cell cellProjectEndDate = row.getCell(6);
			endDate = extractDateCellValue(endDate, cellProjectEndDate);

			String gbUnit = null;
			Cell cellProjectGBUnit = row.getCell(7);
			gbUnit = extractStringCellValue(gbUnit, cellProjectGBUnit);

			String customerGb = null;
			Cell cellProjectCustomerGB = row.getCell(8);
			customerGb = extractStringCellValue(customerGb, cellProjectCustomerGB);

			String members = null;
			Cell cellProjectMembers = row.getCell(9);
			members = extractStringCellValue(members, cellProjectMembers);

			String customerName = null;
			Cell cellProjectCustomerName = row.getCell(10);
			customerName = extractStringCellValue(customerName, cellProjectCustomerName);

			String projectType = null;
			Cell cellProjectType = row.getCell(11);
			projectType = extractStringCellValue(projectType, cellProjectType);

			String leader = null;
			Cell cellProjectLeader = row.getCell(12);
			leader = extractStringCellValue(leader, cellProjectLeader);

			String technologiesUsed = null;
			Cell cellProjectTechnologiesUsed = row.getCell(13);
			technologiesUsed = extractStringCellValue(technologiesUsed, cellProjectTechnologiesUsed);

			String projectObjective = null;
			Cell cellProjectObjective = row.getCell(14);
			projectObjective = extractStringCellValue(projectObjective, cellProjectObjective);

			Double teamSize=null;
			Cell cellProjectTeamSize = row.getCell(15);
			if(!Objects.isNull(cellProjectTeamSize))
				teamSize = getNumbericValue(cellProjectTeamSize);

			String referenceLink = null;
			Cell cellProjectReferenceLink = row.getCell(16);
			referenceLink = extractStringCellValue(referenceLink, cellProjectReferenceLink);

			XLSXProjectDto xlsxProjectDto = XLSXProjectDto.builder()
					.projectName(projectName)
					.projectDescription(projectDescription)
					.status(status)
					.challenge(challenge)
					.startDate(startDate)
					.endDate(endDate)
					.gbUnitGroup(gbUnit)
					.projectObjective(projectObjective)
					.projectType(projectType)
					.technologiesUsed(technologiesUsed)
					.createBy(ntid)
					.teamSize(teamSize)
					.documentLink(referenceLink)
					.vModelPhases(vModelPhasesStr)
					.customerGb(customerGb)
					.member(members)
					.stackHolder(customerName)
					.leader(leader)
					.build();
			xlsxProjectDtos.add(xlsxProjectDto);
    	}
    	return xlsxProjectDtos;
    }
	private static Date extractDateCellValue(Date date, Cell cellProjectStartDate) {
		if(!Objects.isNull(cellProjectStartDate))
			date= getDateValue(cellProjectStartDate);
		return date;
	}
	private static String extractStringCellValue(String result, Cell cellProjectReferenceLink) {
		if(!Objects.isNull(cellProjectReferenceLink))
			result = cellProjectReferenceLink.getStringCellValue().trim();
		return result;
	}
    
    //NOT USED
    @SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	private static <T> T getValueCell(Cell cell) {
    	if(cell.getCellStyle().equals(CellType.STRING)) {
    		return (T) cell.getStringCellValue();
    	}
    	if(cell.getCellStyle().equals(CellType.NUMERIC)) {
    		return (T) (Double) cell.getNumericCellValue();
    	}
    	return (T) cell.getDateCellValue();
    }
    
    //Map between index and name field (NOT USED)
    private static Map<Integer, String> mapHeaderFileXLSX(Row row){
    	// should check header is empty ?
    	Map<Integer, String> mapHeader = new HashMap<Integer, String>();
    	Iterator<Cell> cells = row.iterator();
    	int index =0;
    	while (cells.hasNext()) {
			Cell cell = (Cell) cells.next();
			String tempStringSplit[] = cell.getStringCellValue().split(Constants.WHITE_SPACE);
			String temp=null;
			// if length >= 2 then split white space and concat all string to a single string
			if(tempStringSplit.length>=2) {
				StringBuffer sb = new StringBuffer();
				for (String string : tempStringSplit) {
					sb.append(string);
				}
				temp = sb.toString();
			}
			mapHeader.put(index++, temp == null ? cell.getStringCellValue(): temp);
		}
    	return mapHeader;
    }
    //map data row to object (NOT USED)
    private static XLSXProjectDto mapRow2Object(XLSXProjectDto xlsxProjectDto,Row row, Map<Integer, String> mapHeader) {
//    	XLSXProjectDto xlsxProjectDto = new XLSXProjectDto();
    	for (Cell cell : row) {
			int index= cell.getColumnIndex();
			Field[] fields = XLSXProjectDto.class.getDeclaredFields();
			for (Field field : fields) {
				if(!field.getName().equalsIgnoreCase(mapHeader.get(index))) continue;
				else {
					try {
						field.set(xlsxProjectDto, getValueCell(cell));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
    	return null;
    }
    
    private static Date getDateValue(Cell cell) {
    	try {
			return cell.getDateCellValue();
		} catch (IllegalStateException e) {
			return null;
		}
    }
    private static Double getNumbericValue(Cell cell) {
    	try {
			return cell.getNumericCellValue();
		} catch (IllegalStateException e) {
			return null;
		}
    }
}
