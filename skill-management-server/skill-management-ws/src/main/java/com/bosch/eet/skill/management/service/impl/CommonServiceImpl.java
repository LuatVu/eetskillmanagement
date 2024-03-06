package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.ExcelFileHandler;
import com.bosch.eet.skill.management.common.constant.EmailConstant;
import com.bosch.eet.skill.management.converter.PersonalConverter;
import com.bosch.eet.skill.management.converter.SkillConverter;
import com.bosch.eet.skill.management.converter.utils.CommonTaskConverterUtil;
import com.bosch.eet.skill.management.converter.utils.DepartmentGroupConverterUtil;
import com.bosch.eet.skill.management.converter.utils.PhaseConverterUtil;
import com.bosch.eet.skill.management.converter.utils.ProjectRoleConverterUtil;
import com.bosch.eet.skill.management.converter.utils.TeamConverterUtil;
import com.bosch.eet.skill.management.dto.CommonTaskDto;
import com.bosch.eet.skill.management.dto.DepartmentGroupDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.ProjectRoleDto;
import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.dto.SkillCompetencyLeadDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.dto.VModelDto;
import com.bosch.eet.skill.management.dto.excel.XLSXPersonalSkillClusterDTO;
import com.bosch.eet.skill.management.dto.excel.XLSXPersonalSkillDTO;
import com.bosch.eet.skill.management.dto.excel.XLSXUsersDTO;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.PersonalSpringElasticRepository;
import com.bosch.eet.skill.management.elasticsearch.repo.SkillSpringElasticRepository;
import com.bosch.eet.skill.management.entity.CommonTask;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.PersonalSkillGroup;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.entity.ProjectRole;
import com.bosch.eet.skill.management.entity.RequestEvaluation;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillCompetencyLead;
import com.bosch.eet.skill.management.entity.SkillExperienceLevel;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.exception.EETResponseException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.impl.PersonAttributesMapper;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.repo.CommonTaskRepository;
import com.bosch.eet.skill.management.repo.DepartmentGroupRepository;
import com.bosch.eet.skill.management.repo.DepartmentRepository;
import com.bosch.eet.skill.management.repo.GBRepository;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalProjectRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillGroupRepository;
import com.bosch.eet.skill.management.repo.PhaseRepository;
import com.bosch.eet.skill.management.repo.ProjectRoleRepository;
import com.bosch.eet.skill.management.repo.RequestEvaluationRepository;
import com.bosch.eet.skill.management.repo.SkillCompetencyLeadRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.TeamRepository;
import com.bosch.eet.skill.management.service.CommonService;
import com.bosch.eet.skill.management.specification.CommonTasksSpecification;
import com.bosch.eet.skill.management.usermanagement.consts.MessageCode;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserGroup;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementBusinessException;
import com.bosch.eet.skill.management.usermanagement.repo.GroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserGroupRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final SkillRepository skillRepository;
    private static final String LV0 = "No knowledge or experience in skill area, new in position (in RB-competency-model: 1)";
    private static final String LV1 = "Has basic knowledge of skill area, but has to ask experienced employees for support from time to time (in RB-competency-model: 2 - Generalist)";
    private static final String LV2 = "Knows skill area, is able to identify improvement opportunities and to share them with others (in RB-competency-model: 3 - Expert)";
    private static final String LV3 = "Knows skill area, is able to identify improvement opportunities and to implement them, can act as a coach for this skill area (in RB-competency-model: 4 - Specialist)";
    private static final String LV4 = "Knows skill area, is able to identify improvement opportunities and to implement them, can act as coach for this skill area. Knows interdependencies to other skill areas. (in RB-competency-model: 5 - Champion)";
    private static final String ACTIVE = "Active";
    
    private final ProjectRoleRepository projectRoleRepository;
    private final GBRepository gbRepository;
    private final SkillGroupRepository skillGroupRepository;
    private final SkillExperienceLevelRepository skillExperienceLevelRepository;
    private final LevelRepository levelRepository;
	private final CommonTaskRepository commonTasksRepository;
	private final PersonalProjectRepository personalProjectRepository;
	private final GroupRepository groupRepository;
	private final UserGroupRepository userGroupRepository;
	private final TeamRepository teamRepository;
	private final DepartmentGroupRepository departmentGroupRepository;
	private final RequestEvaluationRepository requestEvaluationRepository;
	private final PhaseRepository phaseRepository;

    private final PersonalRepository personalRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final DepartmentRepository departmentRepository;
    private final SkillLevelRepository skillLevelRepository;
    private final PersonalConverter personalConverter;
    
    private final EmailService emailService;
    private final SkillConverter skillConverter;

    private final PersonalElasticRepository personalElasticRepository;
    private final PersonalSpringElasticRepository personalSpringElasticRepo;
    private final SkillSpringElasticRepository skillSpringElasticRepo;
    private final PersonalSkillGroupRepository personalSkillGroupRepository;
    private final SkillCompetencyLeadRepository skillCompetencyLeadRepository;

    @Autowired
    private LdapTemplate ldapTemplate;
    
    @Override
    public Page<ProjectRoleDto> findAllRoles(Pageable pageable, Map<String, String> q) {
        Page<ProjectRole> personals = projectRoleRepository.findAll(pageable);
        return personals.map(ProjectRoleConverterUtil::convertToDTO);
    }
    @Override
    public List<String> findAllGB() {
        return gbRepository.findAll().stream().map(GbUnit::getName).collect(Collectors.toList());
    }
    
    @Override
    public boolean uploadSkill(MultipartFile file) {
        try {
            HashMap<String, HashMap<String, HashMap<String, Object>>> hashMap = readSkillFromXlsx(file);

            for (String key : hashMap.keySet()) {

                Optional<SkillGroup> skillGroupOptional = skillGroupRepository.findByName(key);
                SkillGroup skillGroup;
                skillGroup = skillGroupOptional.orElseGet(() -> skillGroupRepository.save(SkillGroup.builder()
                        .name(key)
                        .build()));
                HashMap<String, HashMap<String, Object>> hmSkill = hashMap.get(key);
                Set<Skill> skillToSave = new HashSet<>();
                for (String hmSkillKey : hmSkill.keySet()) {
                    if (hmSkillKey.equals("")) {
                        continue;
                    }

                    List<String> hmSkillExperienceDetail = (List<String>) hmSkill.get(hmSkillKey).get("skill_experience_level");
                    Optional<Skill> skillOptional = skillRepository.findByName(hmSkillKey);
                    
                    Skill skill;
                    String isMandatory = (String) hmSkill.get(hmSkillKey).get(Constants.IS_MANDATORY);
                    String isRequired = (String) hmSkill.get(hmSkillKey).get(Constants.IS_REQUIRED);
                    SkillGroup finalSkillGroup = skillGroup;
                    skill = skillOptional.orElseGet(() -> Skill.builder()
                            .name(hmSkillKey)
                            .description(hmSkillKey)
                            .status("Active")
                            .skillGroup(finalSkillGroup)
                            .build());                  
                    
					if (StringUtils.isNotEmpty(isMandatory)) {
						skill.setIsMandatory(isMandatory.equals(Constants.YES) ? true
								: isMandatory.equals(Constants.NO) ? false : null);
					}
					if (StringUtils.isNotEmpty(isRequired)) {
						skill.setIsRequired(isRequired.equals(Constants.YES) ? true
								: isRequired.equals(Constants.NO) ? false : null);
					}

                    skillRepository.save(skill);

					HashMap<String, Object> hmSkillDescription = hmSkill.get(hmSkillKey);

					for (String nameDes : hmSkillDescription.keySet()){
						if(nameDes.equals("skill_experience_level")||(nameDes.equals(Constants.IS_MANDATORY)||(nameDes.equals(Constants.IS_REQUIRED)))) {
							continue;
						}
						Optional<SkillExperienceLevel> skillExperienceLevelOpt = skillExperienceLevelRepository
    							.findByNameAndSkillName(nameDes, skill.getName());
						if(!skillExperienceLevelOpt.isPresent()) {
							skillExperienceLevelRepository.save(
									SkillExperienceLevel.builder().name(nameDes).skillGroup(skillGroup)
									.description(hmSkillDescription.get(nameDes).toString())
									.skill(skill).build()
									);
						}
					}
					 if(hmSkillExperienceDetail != null && skillGroup.equals(skill.getSkillGroup())) {
						Set<String> setSkillExperienceDetail = new HashSet<>(hmSkillExperienceDetail);
						hmSkillExperienceDetail = new ArrayList<>(setSkillExperienceDetail);
		                 for (int k = 0; k < hmSkillExperienceDetail.size(); k++) {
		    					String[] arraySplit = hmSkillExperienceDetail.get(k).split("-");
		    					Level level = levelRepository.findByName(arraySplit[1]).orElse(null);

		    					if(CollectionUtils.isNotEmpty(hmSkillExperienceDetail)&& level!=null) {
		    						SkillLevel sl = SkillLevel.builder().level(level).skill(skill).skillGroup(skillGroup).levelLable(arraySplit[2]).build();
		    						sl = skillLevelRepository.findByLevelAndSkill(level, skill).orElse(sl);
		    						skillLevelRepository.save(sl);
		    					}
						}
					 }
                    skillToSave.add(skill);
					
                    if (hmSkillExperienceDetail == null) {
                        continue;
                    }
                    log.info("");
                }
                skillSpringElasticRepo.saveAll(skillToSave.stream().map(skillConverter::convertToDocument).collect(Collectors.toList()));
            }

            return true;
        } catch (IOException e) {
            throw new EETResponseException(String.valueOf(INTERNAL_SERVER_ERROR.value()), "Skill file is not valid", null);
        }
    }
    
	public HashMap<String, HashMap<String, HashMap<String, Object>>> readSkillFromXlsx(MultipartFile file)
			throws IOException {
		InputStream inputStream = file.getInputStream();
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet skillSheet = workbook.getSheetAt(1);
		HashMap<String, HashMap<String, HashMap<String, Object>>> hashMap = new HashMap<>();
		int i = 0;
		String lastCompentencyName = "";
		for (Row row : skillSheet) {
		    if (i != 0) {
		        log.info("row" + i);
		        HashMap<String, HashMap<String, Object>> valueList;
		        String compentencyName = row.getCell(0) != null ? row.getCell(0).getRichStringCellValue().getString().split("_")[0] : "";

		        if (compentencyName.equals("")) {
		            compentencyName = lastCompentencyName.trim().replaceAll(System.lineSeparator(), "");
		       } else {
		    	   	compentencyName = compentencyName.trim().replaceAll(System.lineSeparator(), "");
		            lastCompentencyName = compentencyName.toUpperCase();
		        }   

		        if (hashMap.containsKey(compentencyName)) {
		            valueList = hashMap.get(compentencyName);
		        } else {
		            valueList = new HashMap<>();
		        }
//                    String skillSet = row.getCell(0) != null ? row.getCell(0).getRichStringCellValue().getString().split("_")[1] : "";
//                    String skillName = row.getCell(1) != null ? row.getCell(1).getRichStringCellValue().getString() : "";
		        String skillName = row.getCell(2) != null ? row.getCell(2).getRichStringCellValue().getString() : "";
		        
		        String isMandatoryStr = row.getCell(4) != null ? row.getCell(4).getRichStringCellValue().getString().trim() : "";
		        String isRequiredStr = row.getCell(5) != null ? row.getCell(5).getRichStringCellValue().getString().trim() : "";
		        String lv0 = row.getCell(6) != null ? row.getCell(6).getRichStringCellValue().getString().trim() : "";
		        String lv1 = row.getCell(7) != null ? row.getCell(7).getRichStringCellValue().getString().trim() : "";
		        String lv2 = row.getCell(8) != null ? row.getCell(8).getRichStringCellValue().getString().trim() : "";
		        String lv3 = row.getCell(9) != null ? row.getCell(9).getRichStringCellValue().getString().trim() : "";
		        String lv4 = row.getCell(10) != null ? row.getCell(10).getRichStringCellValue().getString().trim() : "";
		        HashMap<String, Object> valueHashMap = new HashMap<>();
//                    valueHashMap.put("skillName", skillName);

		        valueHashMap.put(Constants.IS_MANDATORY, isMandatoryStr);
		        valueHashMap.put(Constants.IS_REQUIRED, isRequiredStr);
		        valueHashMap.put(Constants.LEVEL_0, lv0.equals("") ? LV0 : lv0);
		        valueHashMap.put(Constants.LEVEL_1, lv1.equals("") ? LV1 : lv1);
		        valueHashMap.put(Constants.LEVEL_2, lv2.equals("") ? LV2 : lv2);
		        valueHashMap.put(Constants.LEVEL_3, lv3.equals("") ? LV3 : lv3);
		        valueHashMap.put(Constants.LEVEL_4, lv4.equals("") ? LV4 : lv4);
		        valueList.put(skillName, valueHashMap);
		        valueList.put(skillName, valueHashMap);	
		    	hashMap.put(compentencyName.toUpperCase(), valueList);
		    }
		    i++;
		}

		Set<String> setSkills = new HashSet<>();

		Sheet skillLevelSheet = workbook.getSheetAt(2);
		i = 0;
		for (Row row : skillLevelSheet) {
		    if (i > 1) {
		        String skillName = row.getCell(1) != null ? row.getCell(1).getRichStringCellValue().toString() : "";
		        int j = 0;
		        String rawSkillGroup = "";
		        for (Cell cell : row) {
		            if (j > 1) { //before is 0 cell!=null && cell.getCellType()== CellType.BLAN
		                String lv = cell.getRichStringCellValue().getString();
		                if (!lv.equals("") && !lv.equals(" ")) {
		                    CellRangeAddress mergedRegionForCell = getMergedRegionForCell(skillLevelSheet.getRow(0).getCell(cell.getColumnIndex()));
		                    String skillGroup = skillLevelSheet
		                            .getRow(0)
		                            .getCell(mergedRegionForCell.getFirstColumn())
		                            .getRichStringCellValue()
		                            .toString();
		                    String[] labels = skillLevelSheet
		                            .getRow(1)
		                            .getCell(cell.getColumnIndex())
		                            .getRichStringCellValue()
		                            .toString().split(" - ");
		                    String levelLabel = "";
		                    if (null != labels && labels.length > 1) {
		                    	levelLabel = labels[1];
		                    }
		                    levelLabel = levelLabel.replace("\\+", "");
		                    
		                    skillGroup = skillGroup.toUpperCase();
		                    HashMap<String, HashMap<String, Object>> hmSkillGroup = hashMap.get(skillGroup);
		                    
		                    if (hmSkillGroup != null) {
		                        HashMap<String, Object> hmSkillName = hmSkillGroup.get(skillName);
		                        if (hmSkillName != null && !setSkills.contains(skillName)) {
		                            rawSkillGroup = String.valueOf(skillGroup);
		                            AddDataToMap(skillName, lv, skillGroup, levelLabel, hmSkillGroup, hmSkillName);
		                        } else {
		                            if (!rawSkillGroup.equals("")) {
		                                hmSkillGroup = hashMap.get(rawSkillGroup);
		                                hmSkillName = hmSkillGroup.get(skillName);
		                                if (hmSkillName != null && !setSkills.contains(skillName)) {
		                                    AddDataToMap(skillName, lv, skillGroup, levelLabel, hmSkillGroup, hmSkillName);
		                                }
		                            } else {
		                                log.error("skill {} not found: ", skillName);
		                            }
		                        }
		                    }
		                    else {
		                        if (!rawSkillGroup.equals("")) {
		                            hmSkillGroup = hashMap.get(rawSkillGroup);
		                            HashMap<String, Object> hmSkillName = hmSkillGroup.get(skillName);
		                            if (hmSkillName != null && !setSkills.contains(skillName)) {
		                                AddDataToMap(skillName, lv, skillGroup, levelLabel, hmSkillGroup, hmSkillName);
		                            }
		                            hashMap.put(rawSkillGroup, hmSkillGroup);
		                        } else {
		                            log.error("skillGroup {} not found: ", skillGroup);
		                        }
		                    }
		                }
		            }
		            j++;
		        }
		        setSkills.add(skillName);
		    }
		    i++;
		}
		return hashMap;
	}
    
    public String findLevelLableBySkillGroupAndLevel(String skillGroup, String level, List<String> hmSkillExperienceDetail) {
    	for(String item: hmSkillExperienceDetail) {
    		String[] arraySplit = item.split("-");
    		if(arraySplit[0].equals(skillGroup) && arraySplit[1].equals(level)) {
    			return arraySplit[2];
    		}
    	}
    	return null;
    }

    private void AddDataToMap(String skillName, String lv, String skillGroup, String levelLabel, HashMap<String, HashMap<String, Object>> hmSkillGroup, HashMap<String, Object> hmSkillName) {
    	List<String> skillExperienceLevel = (List<String>) hmSkillName.get("skill_experience_level");
        if (skillExperienceLevel == null) {
            skillExperienceLevel = new ArrayList<>();
        }
        skillExperienceLevel.add(skillGroup + "-" + levelLabel + "-" + lv);
        hmSkillName.put("skill_experience_level", skillExperienceLevel);
        hmSkillGroup.put(skillName, hmSkillName);
        log.info("{} - {} - {} - {}", skillGroup, skillName, lv, levelLabel);
    }

    public CellRangeAddress getMergedRegionForCell(Cell c) {
        Sheet s = c.getRow().getSheet();
        for (CellRangeAddress mergedRegion : s.getMergedRegions()) {
            if (mergedRegion.isInRange(c.getRowIndex(), c.getColumnIndex())) {
                // This region contains the cell in question
                return mergedRegion;
            }
        }
        // Not in any
        return null;
    }

    public String findLevelByHashmap(HashMap<String, Object> values, String label) {
        for (String key : values.keySet()) {
            if (String.valueOf(values.get(key)).equals(label)) {
				return key;
			}
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addPersonalInfosFromExcel(MultipartFile file, String createdBy, String departmentName) {
        HashSet<Personal> personalsToSave = new HashSet<>();
        HashSet<User> existedUsers = new HashSet<>();

        try {
            // Get createBy personal
            Optional<Personal> optCreateByPersonal = personalRepository.findByPersonalCodeIgnoreCase(createdBy);
            String createById = optCreateByPersonal.isPresent()? optCreateByPersonal.get().getId() : createdBy;
            List<XLSXUsersDTO> xlsxUsersDTOS = ExcelFileHandler.convertXLSXFileToPersonal(file, createById);

            Level defaultLevel = levelRepository.findLevelByName("L50").orElseThrow(
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

            List<User> allUsers = userRepository.findAll();
            HashMap<String, User> allUsersMap = new HashMap<>();
            for(User user: allUsers){allUsersMap.put(user.getName().toUpperCase(), user);}

            List<Personal> allPersonals = personalRepository.findAll();
            HashMap<String, Personal> allPersonalsMap = new HashMap<>();
            for(Personal personal: allPersonals){allPersonalsMap.put(personal.getPersonalCode().toUpperCase(), personal);}


            xlsxUsersDTOS.forEach(
                    xlsxUsersDTO -> {
                        User user = xlsxUsersDTO.getUser();

                        User userInDB = allUsersMap.get(user.getName().toUpperCase());
                        // case existed user
                        if (userInDB != null) {
                            userInDB.setDisplayName(user.getDisplayName());
                            userInDB.setModifiedDate(LocalDateTime.now());
                            userInDB.setModifiedBy(createdBy);
                            existedUsers.add(userInDB);

                            // Check existence of personal
                            Personal personal = allPersonalsMap.get(userInDB.getId().toUpperCase());
                            if(personal == null){
                                personal =  Personal.builder()
                                        .id(userInDB.getId())
                                        .build();
                            }

                            personal.setPersonalCode(user.getName());
                            personal.setLevel(levelRepository.findLevelByName(xlsxUsersDTO.getGrade()).orElse(defaultLevel));

                            personal.setDepartment(departmentRepository.findByName(departmentName).orElse(null));
                            personal.setExperiencedAtBosch(xlsxUsersDTO.getExperienceAtBosch().toString());
                            personal.setExperiencedNonBosch(xlsxUsersDTO.getNonBoschExp().toString());
                            personal.setTitle(xlsxUsersDTO.getTitle());
                            personal.setTeam(teamRepository.findByName(xlsxUsersDTO.getTeam()).orElse(null));
                            personal.setPersonalNumber(xlsxUsersDTO.getPersonalNumber());
                            personal.setLocation(xlsxUsersDTO.getLocation());
                            personal.setGender(xlsxUsersDTO.getGender().getLabel());
                            personal.setJoinDate(xlsxUsersDTO.getJoinDate());
                            personal.setUpdated(xlsxUsersDTO.getIsUpdated());

                            personalsToSave.add(personal);

                            log.info("user {} has already existed, updating infos", user.getName());

                        // case new user
                        } else {
                            String userId = userRepository.save(user).getId();

                            Personal personal = Personal.builder()
                                    .id(userId)
                                    .personalCode(user.getName())
                                    .personalNumber(xlsxUsersDTO.getPersonalNumber())
                                    .level(levelRepository.findLevelByName(xlsxUsersDTO.getGrade()).orElse(defaultLevel))
                                    .department(departmentRepository.findByName(departmentName).orElse(null))
                                    .experiencedAtBosch(xlsxUsersDTO.getExperienceAtBosch().toString())
                                    .experiencedNonBosch(xlsxUsersDTO.getNonBoschExp().toString())
                                    .team(teamRepository.findByName(xlsxUsersDTO.getTeam()).orElse(null))
                                    .title(xlsxUsersDTO.getTitle())
                                    .joinDate(xlsxUsersDTO.getJoinDate())
                                    .gender(xlsxUsersDTO.getGender().getLabel())
                                    .location(xlsxUsersDTO.getLocation())
                                    .updated(xlsxUsersDTO.getIsUpdated())
                                    .build();

                            personalsToSave.add(personal);

                            log.info("Adding {}", user.getName());
                        }
                    }
            );

//            Update existed user:
            userRepository.saveAll(existedUsers);
//            Save/Update personals
            personalRepository.saveAll(personalsToSave);
//            Sync personals to elastic
            personalSpringElasticRepo.saveAll(personalsToSave.stream().map(personalConverter::convertToDocument).collect(Collectors.toList()));

            log.info("Finished adding personal");
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
    @Transactional(rollbackFor = Exception.class)
    public void addPersonalSkillInfosFromExcel(MultipartFile file, String createdBy, String personalId) {
        try {
            List<XLSXPersonalSkillDTO> xlsxPersonalSkillDTOS =
                    ExcelFileHandler.convertXLSXFileToPersonalSkill(file, createdBy);

            List<Skill> skills = skillRepository.findAll();

            List<Personal> personals = personalRepository.findAll();

			if (!StringUtils.isEmpty(personalId)) {
				xlsxPersonalSkillDTOS = xlsxPersonalSkillDTOS.stream()
						.filter(dto -> dto.getPersonalId().equalsIgnoreCase(personalId)).collect(Collectors.toList());
			}

            xlsxPersonalSkillDTOS.forEach(
                    dto -> {
                        Optional<Personal> personalOpt = personals.stream().filter(
                                p -> p.getPersonalCode().equalsIgnoreCase(dto.getPersonalId())
                        ).findFirst();
                        if (!personalOpt.isPresent()) {
                            log.info("Add personal skill from excel: personal {} not found", dto.getPersonalId());
                            return;
                        }
                        Personal personal = personalOpt.get();

                        Optional<Skill> skillOpt = skills.stream().filter(
                                s -> s.getName().equalsIgnoreCase(dto.getSkillName())
                        ).findFirst();
                        if (!skillOpt.isPresent()) {
                            log.info("Add personal skill from excel: skill {} not found", dto.getSkillName());
                            return;
                        }

                        Skill skill = skillOpt.get();

                        PersonalSkill personalSkill = PersonalSkill.builder()
                                .personal(personal)
                                .skill(skill)
                                .level(Utility.validateSkillLevel(dto.getLevel()))
                                .build();

                        personal.getPersonalSkills().add(personalSkill);
                        skill.getPersonalSkill().add(personalSkill);

                        personalRepository.save(personal);
                        personalElasticRepository.updatePersonal(personal);

                        log.info("Added skill {} to personal {}", dto.getSkillName(), dto.getPersonalId());
                    }
            );
            log.info("Finished adding skill to personal");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new SkillManagementException(
                    MessageCode.HANDLE_EXCEL_FAIL.toString(),
                    messageSource.getMessage(
                            MessageCode.HANDLE_EXCEL_FAIL.toString(),
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    null
            );
        }
    }

	 @Override
	    public Page<CommonTaskDto> findByProjectRoleId(String Id, Pageable pageable) {
	        HashMap<String, String> hmSearch = new HashMap<>();
	        hmSearch.put("project_role_id", Id);
	        Specification<CommonTask> specification = CommonTasksSpecification.search(hmSearch);
	        Page<CommonTask> commontask = commonTasksRepository.findAll(specification, pageable);
	        return commontask.map(CommonTaskConverterUtil::convertToDTO);
	    }
	@Override
	public List<CommonTaskDto> findAllCommonTask() {
		List<CommonTask> tasks = commonTasksRepository.findAll();
		log.info(tasks.toString());
		return CommonTaskConverterUtil.convertToDTOs(tasks);
	}
	
	//Delete project role
	@Override
	public List<ProjectRoleDto> deleteProjectRole(List<ProjectRoleDto> projectRoleDtos) {
		for (ProjectRoleDto projectRoleDto : projectRoleDtos) {
			String projectRoleId = projectRoleDto.getId();
			Optional<ProjectRole> projectRoleOpt = projectRoleRepository.findById(projectRoleId);
			if (!projectRoleOpt.isPresent()) {
				throw new SkillManagementException(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()),
						null, NOT_FOUND);
			}
			List<PersonalProject> personalProjects = personalProjectRepository.findByProjectRoleId(projectRoleId);
			if (!personalProjects.isEmpty()) {
				throw new SkillManagementException(MessageCode.PROJECT_ROLE_IS_IN_USE.toString(),
						messageSource.getMessage(MessageCode.PROJECT_ROLE_IS_IN_USE.toString(), null,
								LocaleContextHolder.getLocale()),
						null, NOT_FOUND);
			} else {
				projectRoleRepository.deleteById(projectRoleId);
			}
		}
		return projectRoleDtos;
	}
	
	@Override
	public List<CommonTaskDto> deleteCommonTask(List<CommonTaskDto> commonTaskDtos) {
		for (CommonTaskDto commonTaskDto : commonTaskDtos) {
			String commonTaskId = commonTaskDto.getId();
			Optional<CommonTask> commonTaskOpt = commonTasksRepository.findById(commonTaskId);
			if (!commonTaskOpt.isPresent()) {
				throw new SkillManagementException(MessageCode.COMMON_TASK_NOT_FOUND.toString(),
						messageSource.getMessage(MessageCode.COMMON_TASK_NOT_FOUND.toString(), null,
								LocaleContextHolder.getLocale()),
						null, NOT_FOUND);
			}
			commonTasksRepository.deleteById(commonTaskId);
		}		
		return commonTaskDtos;		
	}
	
	@Override
	public ProjectRoleDto addNewProjectRole (ProjectRoleDto projectRoleDto) {
		Optional<ProjectRole> projectRole = projectRoleRepository.findByName(projectRoleDto.getName());
		if (projectRole.isPresent()) {
			log.info("hihi");
			throw new SkillManagementException(MessageCode.PROJECT_NAME_ALREADY_EXIST.toString(),
					messageSource.getMessage(MessageCode.PROJECT_NAME_ALREADY_EXIST.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
		ProjectRole newProjectRole = ProjectRole.builder()
				.id(projectRoleDto.getId())
				.name(projectRoleDto.getName())
				.status(ACTIVE)
				.build();			
		
		final ProjectRole savedProjectRole = projectRoleRepository.save(newProjectRole);
		return ProjectRoleConverterUtil.convertToDTO(savedProjectRole);		
	}
	
	@Override
	public CommonTaskDto addNewCommonTask(CommonTaskDto commonTaskDto) {
		try {
			Optional<ProjectRole> projectRoleOpt = projectRoleRepository.findById(commonTaskDto.getProject_role_id());
			if (!projectRoleOpt.isPresent()) {
				throw new SkillManagementException(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(), messageSource
						.getMessage(MessageCode.PROJECT_ROLE_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale()),
						null, NOT_FOUND);
			}
			ProjectRole projectRole = projectRoleOpt.get();
			projectRole.getCommonTask().forEach(commonTask -> {
				if (commonTask.getName().equals(commonTaskDto.getName())) {
					throw new SkillManagementException(MessageCode.COMMON_TASK_AREADY_EXIST.toString(),
							messageSource.getMessage(MessageCode.COMMON_TASK_AREADY_EXIST.toString(), null,
									LocaleContextHolder.getLocale()),
							null, NOT_FOUND);
				}
			});

			CommonTask newcommonTask = CommonTask.builder()
					.id(commonTaskDto.getId())
					.name(commonTaskDto.getName().trim())
					.projectRole(projectRole)
					.build();
			final CommonTask savedCommonTask = commonTasksRepository.save(newcommonTask);
			return CommonTaskConverterUtil.convertToDTO(savedCommonTask);
		}
		catch(SkillManagementException skillManagementException) {
			log.error(skillManagementException.getMessage());
			throw skillManagementException;
		}
		catch(Exception e) {
			log.error(e.getMessage());
			throw new SkillManagementException(MessageCode.SKM_COMMON_TASK_ADD_FAIL.toString(),
					messageSource.getMessage(MessageCode.SKM_COMMON_TASK_ADD_FAIL.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
	}
	
	@Override
	public List<PersonalDto> findAllPersonalManagerRole() {
		List<User> listManager = new ArrayList<>();
		Optional<Group> groupOpt = groupRepository.findByName(Constants.MANAGER);
		if(!groupOpt.isPresent()) {
			throw new SkillManagementException(MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(),
					messageSource.getMessage(MessageCode.SKM_GROUP_NOT_EXIST_MSG.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
		Group group = groupOpt.get();
		String groupId = group.getId();
		List<UserGroup> usersInGroup = userGroupRepository.findByGroupId(groupId);
		for(UserGroup userGroup : usersInGroup) {
			User user = userGroup.getUser();
			listManager.add(user);
		}
		return personalConverter.userConvertToPersonalDtos(listManager);
	}
	
	@Override
	public List<TeamDto> findAllTeam() {
		List<Team> listTeam = teamRepository.findAll();
		listTeam.sort(Comparator.comparing(Team::getName));
		return TeamConverterUtil.convertToDTOS(listTeam);
	}
	
	@Override
	public void addSkillCluster(MultipartFile file, String createdBy, String personalId) {
        try {
        	StopWatch stopWatch = new StopWatch();
        	log.info("Start adding skill cluster to personal");
            List<XLSXPersonalSkillClusterDTO> xlsxPersonalSkillClusterDTOS =
                    ExcelFileHandler.convertXLSXFileToPersonalSkillCluster(file, createdBy);
			if (CollectionUtils.isNotEmpty(xlsxPersonalSkillClusterDTOS)) {
				List<SkillGroup> skillGroups = skillGroupRepository.findAll();
				stopWatch.start();
				List<Personal> personals = personalRepository.findAll();
				stopWatch.stop();
				log.info("addSkillCluster " + stopWatch.getTotalTimeMillis() + "ms ~= "
						+ stopWatch.getTotalTimeSeconds() + "s ~=");
				stopWatch.start();
				if (!StringUtils.isEmpty(personalId)) {
					xlsxPersonalSkillClusterDTOS = xlsxPersonalSkillClusterDTOS.stream()
							.filter(dto -> dto.getPersonalId().equalsIgnoreCase(personalId))
							.collect(Collectors.toList());
				}

                List<PersonalSkillGroup> allPersonalSkillGroups = new ArrayList<>();
				xlsxPersonalSkillClusterDTOS.forEach(dto -> {
					Optional<Personal> personalOpt = personals.stream()
							.filter(p -> p.getPersonalCode().equalsIgnoreCase(dto.getPersonalId())).findFirst();
					if (!personalOpt.isPresent()) {
						log.error("Add personal skill from excel: personal {} not found", dto.getPersonalId());
						return;
					}
					Personal personal = personalOpt.get();
					List<String> skillGroupIds = new ArrayList<>();
					for (String skillCluster : dto.getSkillCluster()) {
						Optional<SkillGroup> skillOpt = skillGroups.stream()
								.filter(s -> s.getName().equalsIgnoreCase(skillCluster)).findFirst();
						if (!skillOpt.isPresent()) {
							log.error("Add personal skill from excel: skill group {} not found", skillCluster);
							return;
						}

						SkillGroup skillGroup = skillOpt.get();
						skillGroupIds.add(skillGroup.getId());
                        allPersonalSkillGroups.add(PersonalSkillGroup.builder()
                                .skillGroup(skillGroup)
                                .personal(personal)
                                .build());
					}
					String mainSkillCluster = String.join(",", skillGroupIds);
					personal.setMainSkillCluster(mainSkillCluster);

					personalRepository.save(personal);
                    personalElasticRepository.updatePersonal(personal);

                    log.info("Added skill clusters {} to personal {}", dto.getSkillCluster(), dto.getPersonalId());
				});
                personalSkillGroupRepository.saveAll(allPersonalSkillGroups);

				stopWatch.stop();
				log.info("addSkillCluster " + stopWatch.getTotalTimeMillis() + "ms ~= "
						+ stopWatch.getTotalTimeSeconds() + "s ~=");
				log.info("Finished adding skill cluster to personal");
			}
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
	}
	@Override
	public List<DepartmentGroupDto> findAllDepartmentGroup() {
		return DepartmentGroupConverterUtil.convertToDTOS(departmentGroupRepository.findAll());
	}
	
	@Override
	public DepartmentGroupDto findByTeamId(String id) {
		Optional<Team> teamOpt = teamRepository.findById(id);
		if(!teamOpt.isPresent()) {
			throw new SkillManagementException(MessageCode.TEAM_NOT_EXIST.toString(),
					messageSource.getMessage(MessageCode.TEAM_NOT_EXIST.toString(), null,
							LocaleContextHolder.getLocale()),
					null, NOT_FOUND);
		}
		Team team = teamOpt.get();
		return DepartmentGroupConverterUtil.convertToSimpleDto(team.getDepartmentGroup());

	}
	
	@Override
	@Scheduled(cron = "${cron.timestamp}")
	public void sendMailRequestPending() {
		List<RequestEvaluationPendingDto> list = requestEvaluationRepository.findRequestPendingNew();
		for (RequestEvaluationPendingDto repDto : list) {
            if(Objects.isNull(repDto.getMail())) {continue;}
			emailService.mailPendingRequest(repDto.getDisplayName(), repDto.getMail(), repDto.getCount(),
					Constants.MANAGE_PENDING_REQUEST_LINK, EmailConstant.SPRING_MAIL_TEMPLATE_REQUEST_PENDING, EmailConstant.REQUEST_PENDING);
		}
	}
	
	@Override
	public PersonalDto findLineManager(String idPersonal) {
		Optional<User> managerOpt = personalRepository.findLineManager(idPersonal);
		if(!managerOpt.isPresent()) {
			return new PersonalDto();
		}
		PersonalDto manager = personalConverter.userConvertToPersonalDto(managerOpt.get());
		return manager;
	}
	@Override
	public VModelDto getVModel() {
		List<Phase> phase = phaseRepository.findAll();
		return PhaseConverterUtil.convertToDtos(phase);
    }
    
    @Override
	@Transactional
	public String updateEmail() throws LdapException {
		LdapInfo ldapInfo = null;
		List<User> users = userRepository.findAll();
		for (User user : users) {
			try {
	            List<LdapInfo> ldapInfos = ldapTemplate.search(query().where(Constants.OBJECT_CLASS).is(Constants.PERSON).and(Constants.CN).is(user.getName()), new PersonAttributesMapper());
	            if (!CollectionUtils.isEmpty(ldapInfos)) {
	                ldapInfo = ldapInfos.iterator().next();
	                user.setEmail(ldapInfo.getEmail());
	                log.info(ldapInfo.toString());
	            }
	        } catch (Exception ex) {
	            log.error(ex.getMessage());
	            throw new LdapException("CAN_NOT_CONNECT_LDAP_SERVER", "Can not connect to LDAP Server.", ex.getCause());
	        }
		}
		userRepository.saveAll(users);
		return "Done";
	}

    @Override
    @Transactional
    public String updatePersonalSkillGroup() {
        Map<String, SkillGroup> skillGroupMap = skillGroupRepository.findAll()
                .stream().collect(Collectors.toMap(SkillGroup::getId, skillGroup -> skillGroup));
        List<String> personalIds = personalRepository.findAllPersonalId();
        List<PersonalSkillGroup> personalSkillGroups = new ArrayList<>();

        for(String personalId: personalIds){
            Personal personal = personalRepository.findById(personalId).orElse(null);
            if(Objects.isNull(personal)){
                throw new SkillManagementException(com.bosch.eet.skill.management.exception.MessageCode.SKM_USER_NOT_FOUND.toString(),
                        messageSource.getMessage(com.bosch.eet.skill.management.exception.MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
            }
            String mainSkillClusterStr = personal.getMainSkillCluster();
            if(StringUtils.isBlank(mainSkillClusterStr)){
                continue;
            }

            List<PersonalSkillGroup> eachPersonalSkillGroups = personal.getPersonalSkillGroups();
            Set<String> dbSkillGroupIds = new HashSet<>();
            if(CollectionUtils.isNotEmpty(eachPersonalSkillGroups)){
                eachPersonalSkillGroups.forEach(item -> {
                    dbSkillGroupIds.add(item.getSkillGroup().getId());
                });
            }

            List<String> skillGroupIds = Arrays.asList(mainSkillClusterStr.split(Constants.COMMA));
            skillGroupIds.forEach(skillGroupId -> {
                if(dbSkillGroupIds.contains(skillGroupId)){ return; }

                SkillGroup skillGroup = skillGroupMap.get(skillGroupId);
                if(Objects.isNull(skillGroup)){
                    throw new SkillManagementException(com.bosch.eet.skill.management.exception.MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(),
                            messageSource.getMessage(com.bosch.eet.skill.management.exception.MessageCode.SKM_SKILL_GROUP_NOT_FOUND.toString(), null,
                                    LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
                }
                personalSkillGroups.add(PersonalSkillGroup.builder()
                        .personal(personal)
                        .skillGroup(skillGroup)
                        .build());
            });
        }
        personalSkillGroupRepository.saveAll(personalSkillGroups);
        return MessageCode.SUCCESS.toString();
    }

    @Override
    public List<SkillCompetencyLeadDto> findCompetencyLeadByRequest(String requestId){
        RequestEvaluation requestEvaluation = requestEvaluationRepository.findById(requestId).orElse(null);
        if(Objects.isNull(requestEvaluation)){
            throw new SkillManagementException(com.bosch.eet.skill.management.exception.MessageCode.REQUEST_EVALUATION_NOT_FOUND.toString(),
                    messageSource.getMessage(com.bosch.eet.skill.management.exception.MessageCode.REQUEST_EVALUATION_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }

        Set<String> skillGroupIds = new HashSet<>();

        requestEvaluation.getRequestEvaluationDetails().forEach(detail -> {
//            boolean isForwarded = false;
//            if(!detail.getApprover().getId().equalsIgnoreCase(requestEvaluation.getApprover().getId())) {
//                isForwarded = true;
//            }
            if(detail.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_APPROVAL)
//                    || !isForwarded
            ){
                skillGroupIds.add(detail.getSkill().getSkillGroup().getId());
            }
        });
        List<SkillCompetencyLead> skillCompetencyLeads = new ArrayList<>();
        skillGroupIds.forEach(skillGroupId -> {
            skillCompetencyLeads.addAll(skillCompetencyLeadRepository.findDistinctBySkillGroupId(skillGroupId));
        });
        return skillCompetencyLeads.stream().map(item -> SkillCompetencyLeadDto.builder()
                .personalId(item.getPersonal().getId())
                .displayName(item.getPersonal().getUser().getDisplayName())
                .skillCluster(item.getSkillGroup().getName())
                .skillGroupId(item.getSkillGroup().getId())
                .build()).collect(Collectors.toList());
    }

}
