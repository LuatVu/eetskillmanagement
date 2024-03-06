package com.bosch.eet.skill.management.converter;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.Gender;
import com.bosch.eet.skill.management.dto.AddSkillDto;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillLevelDTO;
import com.bosch.eet.skill.management.elasticsearch.document.PersonalDocument;
import com.bosch.eet.skill.management.entity.Department;
import com.bosch.eet.skill.management.entity.DepartmentGroup;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.PersonalSkillGroup;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillLevel;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.util.Utility;
import com.bosch.eet.skill.management.repo.DepartmentRepository;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalSkillRepository;
import com.bosch.eet.skill.management.repo.SkillExperienceLevelRepository;
import com.bosch.eet.skill.management.repo.SkillLevelRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.SkillsHighlightReposistory;
import com.bosch.eet.skill.management.repo.TeamRepository;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonalConverter {

	@Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonalSkillRepository personalSkillRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private SkillExperienceLevelRepository skillExperienceLevelRepository;
    
    @Autowired
    private SkillRepository skillRepository;
    
    @Autowired 
    private SkillsHighlightReposistory skillsHighlightReposistory;
    
    @Autowired
    private SkillLevelRepository skillLevelRepository;
    
    public PersonalDto convertToBaseDTO(Personal personal){
        int exp = 0;
        if (!Objects.isNull(personal.getExperiencedAtBosch())) {
            try {
                int boschExp = Integer.parseInt(personal.getExperiencedAtBosch());
                exp += boschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Objects.isNull(personal.getExperiencedNonBosch())) {
            try {
                int nonBoschExp = Integer.parseInt(personal.getExperiencedNonBosch());
                exp += nonBoschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String groupName, groupId, teamName, teamId, managerDisplayName, managerId;
        groupName = groupId = teamName = teamId = managerDisplayName = managerId  = null;
        Team team = personal.getTeam();
        if(Objects.nonNull(team)){
            teamName = team.getName();
            teamId = team.getId();

            DepartmentGroup group = team.getDepartmentGroup();
            if(Objects.nonNull(group)){
                groupId = group.getId();
                groupName = group.getName();
            }

            User lineManager = team.getLineManager();
            if(Objects.nonNull(lineManager)){
                managerDisplayName = lineManager.getDisplayName();
                managerId = lineManager.getId();
            }
        }

        String levelName, levelId;
        levelName = levelId = null;
        Level level = personal.getLevel();
        if(Objects.nonNull(level)){
            levelName = level.getName();
            levelId = level.getId();
        }

        String deptId, deptName;
        deptId = deptName = null;
        Department department = personal.getDepartment();
        if(Objects.nonNull(department)){
            deptId = department.getId();
            deptName = department.getName();
        }
        String gender = personal.getGender();
        User user = personal.getUser();
        String email, name;
        email = name = StringUtils.EMPTY;
        if(Objects.nonNull(user)) {
            email = user.getEmail();
            name = user.getDisplayName();
        }

        return PersonalDto.builder()
                .id(personal.getId())
                .name(name)
                .email(email)
                .personalNumber(personal.getPersonalNumber())
                .briefInfo(personal.getBriefInfo())
                .code(personal.getPersonalCode())
                .manager(managerId)
                .title(personal.getTitle())
                .picture(personal.getPicture())
                .group(groupName)
                .groupId(groupId)
                .teamId(teamId)
                .team(teamName)
                .supervisorName(managerDisplayName)
                .levelId(levelId)
                .level(levelName)
                .experiencedAtBosch(personal.getExperiencedAtBosch())
                .experiencedNonBosch(personal.getExperiencedNonBosch())
                .totalExperienced(String.valueOf(exp))
                .departmentName(deptName)
                .department(deptId)
                .gender(Gender.getGenderByLabel(gender).getValue())
                .genderCode(Gender.getGenderByLabel(gender).getLabel())
                .location(personal.getLocation())
                .joinDate(personal.getJoinDate())
                .build();
    }

    @Deprecated
    public PersonalDto convertToDTO(Personal personal) {
        PersonalDto personalDto = convertToBaseDTO(personal);
        List<SkillDto> skills = new ArrayList<>();
        if (personal.getPersonalSkills() != null && !personal.getPersonalSkills().isEmpty()) {
            for (PersonalSkill personalSkill : personal.getPersonalSkills()) {
                Skill skill = personalSkill.getSkill();
                if (skill != null) {
                    List<SkillLevelDTO> expectedSkillLevelDTOS = skillExperienceLevelRepository.findBySkillAndLevel(skill, personal.getLevel())
                            .stream().map(
                                    skillExperienceLevel -> SkillLevelDTO.builder()
                                            .description(skillExperienceLevel.getDescription())
                                            .level(skillExperienceLevel.getName())
                                            .build()
                            ).collect(Collectors.toList());
                    String expectedLevel = "";
                    if (!expectedSkillLevelDTOS.isEmpty()) {
                        String[] levelSplit = expectedSkillLevelDTOS.get(0).getLevel().split(" ");
                        if (levelSplit.length > 1) {
                            expectedLevel = levelSplit[1];
                        }
                    }
                    SkillGroup skillGroup = skill.getSkillGroup();
                    String skillGroupName = Objects.isNull(skillGroup) ?
                            StringUtils.EMPTY : skillGroup.getName();
                    skills.add(SkillDto.builder()
                            .id(skill.getId())
                            .name(skill.getName())
                            .competency(skill.getDescription())
                            .level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
                            .skillGroup(skillGroupName)
                            .experienceNumber(personalSkill.getExperience())
                            .expectedLevel(expectedLevel)
                            .build());
                }
            }
        }
        List<SkillGroup> skillGroups = getSkillGroupsFromPersonal(personal);
        personalDto.setSkillClusterId(getSkillGroupIds(skillGroups));
        personalDto.setSkillCluster(getSkillGroupNames(skillGroups));
        personalDto.setSkills(skills);
        List<SkillDto> sortSkills = new ArrayList<>(skills);
        sortSkills.sort(Comparator.comparing(SkillDto::getName).reversed());
        sortSkills.sort(Comparator.comparing(SkillDto::getLevel).reversed());
        personalDto.setTopSkills(sortSkills.stream().limit(6).collect(Collectors.toList()));
        return personalDto;
    }
    
    public PersonalDto convertToDTOV2(Personal personal) {
        PersonalDto personalDto = convertToBaseDTO(personal);
        List<SkillDto> skills = mapAllSkillAndPersonalSkill(personal);
        
        List<String> idSkillHighlights = skillsHighlightReposistory.findIdSkillHightlightByPersonalId(personal.getId());
        List<SkillGroup> skillGroups = getSkillGroupsFromPersonal(personal);
        personalDto.setSkillClusterId(getSkillGroupIds(skillGroups));
        personalDto.setSkillCluster(getSkillGroupNames(skillGroups));
        personalDto.setSkills(skills);
        List<SkillDto> sortSkills = new ArrayList<>(skills);
        sortSkills.sort(Comparator.comparing(SkillDto::getName).reversed());
        sortSkills.sort(Comparator.comparing(SkillDto::getLevel).reversed());
        List<SkillDto> topSkills = sortSkills.stream().filter(item -> idSkillHighlights.contains(item.getId())).limit(6).collect(Collectors.toList());
        personalDto.setTopSkills(topSkills);
        return personalDto;
    }

    private List<SkillDto> mapAllSkillAndPersonalSkill(Personal personal){
        List<SkillLevel> skillLevel = new ArrayList<>();
        if(personal.getLevel() != null) {
            skillLevel = skillLevelRepository.findExpectedLevelByAssociateLevelId(personal.getLevel().getId());
        }
        HashMap<String, String> hmExpectedLevel = new HashMap<>();
        for (SkillLevel sl : skillLevel) {
            hmExpectedLevel.put(sl.getSkill().getId(), sl.getLevelLable());
        }

        List<SkillDto> skills = new ArrayList<>();
        Set<PersonalSkill> personalSkills = personal.getPersonalSkills();
        Map<String, PersonalSkill> personalSkillsMapById = new HashMap<>();
        if (!CollectionUtils.isEmpty(personalSkills)) {
            personalSkillsMapById = personalSkills.stream()
                    .collect(Collectors.toMap(personalSkill ->
                                    personalSkill.getSkill().getId(),
                            personalSkill -> personalSkill));
        }

        List<Skill> allSkills = skillRepository.findAll();

        for(Skill skill: allSkills) {
            String skillId = skill.getId();
            String expectedLevel = "";
            if (hmExpectedLevel.containsKey(skillId)) {
                String[] levelSplit = hmExpectedLevel.get(skillId).split(" ");
                expectedLevel = levelSplit[1];
            }

            String level = Constants.DEFAULT_SKILL_LEVEL;
            int experience = Constants.DEFAULT_EXPERIENCE;
            PersonalSkill personalSkill = personalSkillsMapById.get(skillId);
            if(Objects.nonNull(personalSkill)){
                level = Utility.floatToStringLevelFormat(personalSkill.getLevel());
                experience = personalSkill.getExperience();
            }

            SkillGroup skillGroup = skill.getSkillGroup();
            skills.add(SkillDto.builder()
                    .id(skillId)
                    .name(skill.getName())
                    .competency(skill.getDescription())
                    .level(level)
                    .skillGroup(skillGroup.getName())
                    .experienceNumber(experience)
                    .expectedLevel(expectedLevel)
                    .skillType(skillGroup.getSkillType().getName())
                    .build());
        }
        return skills;
    }
    
    public PersonalDto convertToAddSkillDto(Personal personal) {
        PersonalDto personalDto = convertToBaseDTO(personal);

        List<SkillGroup> skillGroups = getSkillGroupsFromPersonal(personal);
        personalDto.setSkillClusterId(getSkillGroupIds(skillGroups));
        personalDto.setSkillCluster(getSkillGroupNames(skillGroups));
        return personalDto;
    }
    
    public PersonalDto convertToPDFDTO (Personal personal) {
        PersonalDto personalDto = convertToBaseDTO(personal);
        List<SkillLevel> skillLevel = skillLevelRepository.findExpectedLevel(personal.getId(), personal.getLevel().getId());     
        
        HashMap<String, String> hmExpectedLevel = new HashMap<>();
        
        for (SkillLevel sl : skillLevel) {
      	  hmExpectedLevel.put(sl.getSkill().getId(), sl.getLevelLable());
        }

        List<SkillDto> skills = new ArrayList<>();
        if (!CollectionUtils.isEmpty(personal.getPersonalSkills())) {
        	for (PersonalSkill personalSkill : personal.getPersonalSkills()) {
                if (personalSkill.getSkill() != null) {
                	Skill skill = personalSkill.getSkill();
                	String expectedLevel="";
                	if(hmExpectedLevel.containsKey(skill.getId())) {
                		String[] levelSplit = hmExpectedLevel.get(skill.getId()).split(" ");                      
                        expectedLevel = levelSplit[1];
                	}
                	String skillGroupName = Objects.isNull(skill.getSkillGroup()) ?
                              StringUtils.EMPTY : skill.getSkillGroup().getName();
                    skills.add(SkillDto.builder()
                              .id(skill.getId())
                              .name(skill.getName())
                              .competency(skill.getDescription())
                              .level(Utility.floatToStringLevelFormat(personalSkill.getLevel()))
                              .skillGroup(skillGroupName)
                              .experienceNumber(personalSkill.getExperience())
                              .expectedLevel(expectedLevel)
                              .build());
                }
        	}
        }
        
        List<String> idSkillHighlights = skillsHighlightReposistory.findIdSkillHightlightByPersonalId(personal.getId());
        List<SkillGroup> skillGroups = getSkillGroupsFromPersonal(personal);
        personalDto.setSkillClusterId(getSkillGroupIds(skillGroups));
        personalDto.setSkillCluster(getSkillGroupNames(skillGroups));
        personalDto.setSkills(skills.stream().filter(item-> Integer.parseInt(item.getLevel())>=1).collect(Collectors.toList()));
        List<SkillDto> sortSkills = new ArrayList<>(skills);
        sortSkills.sort(Comparator.comparing(SkillDto::getName).reversed());
        sortSkills.sort(Comparator.comparing(SkillDto::getLevel).reversed());
        List<SkillDto> topSkills = sortSkills.stream().filter(item -> idSkillHighlights.contains(item.getId())).limit(6).collect(Collectors.toList());
        personalDto.setTopSkills(topSkills);
        return personalDto;
    }
    
    public PersonalDto convertToManagerDto(Personal personal) {
    	return PersonalDto.builder()
    			.id(personal.getId())
    			.name(personal.getUser().getDisplayName())
    			.code(personal.getPersonalCode())
    			.build();
    }
    
    public List<PersonalDto> convertToListMangerDto(List<Personal> listPersonal) {
        List<PersonalDto> personalsDto = new ArrayList<>();
        for (Personal personal : listPersonal) {
            personalsDto.add(convertToManagerDto(personal));
        }
        return personalsDto;
    }
    
    public PersonalDto userConvertToPersonalDto(User user) {
    	return PersonalDto.builder()
    			.id(user.getId())
    			.name(user.getDisplayName())
    			.build();
    }
    
    public List<PersonalDto> userConvertToPersonalDtos(List<User> listUser){
        List<PersonalDto> personalsDto = new ArrayList<>();
        for (User user : listUser) {
            personalsDto.add(userConvertToPersonalDto(user));
        }
        return personalsDto;

    }

    public PersonalDto convertToSearchDTO(Personal personal) {
        int exp = 0;
        if (!Objects.isNull(personal.getExperiencedAtBosch())) {
            try {
                int boschExp = Integer.parseInt(personal.getExperiencedAtBosch());
                exp += boschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Objects.isNull(personal.getExperiencedNonBosch())) {
            try {
                int nonBoschExp = Integer.parseInt(personal.getExperiencedNonBosch());
                exp += nonBoschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String name = Objects.isNull(personal.getUser()) ? null : personal.getUser().getDisplayName();
        Level level = personal.getLevel();
        Team team = personal.getTeam();

        PersonalDto personalDto = PersonalDto.builder()
                .id(personal.getId())
                .name(name)
                .code(personal.getPersonalCode())
                .level(Objects.nonNull(level) ? level.getName() : null)
                .team(Objects.nonNull(team) ? team.getName() : null)
                .totalExperienced(String.valueOf(exp))
                .gender(Gender.getGenderByLabel(personal.getGender()).getValue())
                .location(personal.getLocation())
                .joinDate(personal.getJoinDate())
                .build();
        Set<String> skillGroup = new HashSet<>();
        StringBuilder skillStrBuilder = new StringBuilder();
        Set<PersonalSkill> personalSkills = personal.getPersonalSkills();
        if (!CollectionUtils.isEmpty(personalSkills)) {
            for (PersonalSkill personalSkill : personalSkills) {
                if (personalSkill.getSkill() != null) {
                    Skill skill = personalSkill.getSkill();
                    skillGroup.add(skill.getSkillGroup().getName());
                    skillStrBuilder.append(skill.getName()).append(",");
                }
            }
        }
        if (StringUtils.isNotEmpty(skillStrBuilder.toString())) {
            skillStrBuilder.deleteCharAt(skillStrBuilder.length() - 1);
        }
        personalDto.setSkill(skillStrBuilder.toString());
        personalDto.setSkillGroup(String.join(",", skillGroup));
        return personalDto;
    }

    public List<PersonalDto> convertToDTOs(List<Personal> personals) {
        List<PersonalDto> personalsDto = new ArrayList<>();
        for (Personal personal : personals) {
            personalsDto.add(convertToDTO(personal));
        }
        return personalsDto;
    }
    
    public Personal mapDtoToEntity(PersonalDto personalDto) {
    	Optional<User> userOpt = userRepository.findById(personalDto.getId());
    	if(!userOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
    	}
    	Optional<User> managerOpt = userRepository.findById(personalDto.getManager());
    	if(!managerOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
    	}
    	Optional<Department> departmentOpt = departmentRepository.findById(personalDto.getDepartment());
    	if(!departmentOpt.isPresent()) {
                throw new SkillManagementException(MessageCode.SKM_DEPARTMENT_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKM_DEPARTMENT_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, NOT_FOUND);
        	}
    	Optional<Level> levelOpt = levelRepository.findById(personalDto.getLevelId());
    	if(!levelOpt.isPresent()) {
                throw new SkillManagementException(MessageCode.LEVEL_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.LEVEL_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, NOT_FOUND);
        	}
    	Optional<Team> teamOpt = teamRepository.findById(personalDto.getTeam());
    	if (!teamOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_TEAM_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_TEAM_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
    	String skillCluster = personalDto.getSkillClusterId().toString();

    	return Personal.builder()
    			.id(personalDto.getId())
    			.level(levelOpt.get())
    			.personalCode(personalDto.getCode())
    			.gender(personalDto.getGender())
    			.department(departmentOpt.get())
    			.title(personalDto.getTitle())
    			.team(teamOpt.get())
    			.location(personalDto.getLocation())
    			.joinDate(personalDto.getJoinDate())
    			.personalNumber(personalDto.getPersonalNumber())
    			.manager(managerOpt.get())
    			.experiencedNonBosch(personalDto.getExperiencedNonBosch())
    			.mainSkillCluster(skillCluster)
    			.updated(false)
    			.build();
    }
    
    public PersonalDocument convertToDocument(Personal personal) {
        int exp = 0;
        if (!Objects.isNull(personal.getExperiencedAtBosch())) {
            try {
                int boschExp = Integer.parseInt(personal.getExperiencedAtBosch());
                exp += boschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Objects.isNull(personal.getExperiencedNonBosch())) {
            try {
                int nonBoschExp = Integer.parseInt(personal.getExperiencedNonBosch());
                exp += nonBoschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    	Optional<User> userOpt = userRepository.findById(personal.getId());
    	if(!userOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
    	}
    	
    	List<PersonalSkill> listSkills = personalSkillRepository.findByPersonalId(personal.getId());
    	List<String> skills = new ArrayList<>();
    	for (PersonalSkill ps : listSkills) {
    		if(ps.getLevel() != Constants.DEFAULT_FLOAT_SKILL_LEVEL) {
	    		String skillName = ps.getSkill().getName();
	    		skills.add(skillName);
    		}
    	}
        String personalId = personal.getId();
        Team team = personal.getTeam();
        Level level = personal.getLevel();
        Department department = personal.getDepartment();

        return PersonalDocument.builder()
            .id(personalId)
            .personalId(personalId)
            .personalCode(personal.getPersonalCode())
            .displayName(userOpt.get().getDisplayName())
            .team(Objects.nonNull(team) ? team.getName() : null)
            .level(Objects.nonNull(level) ? level.getName() : null)
            .experience(exp)
            .department(Objects.nonNull(department) ? department.getName() : null)
            .location(personal.getLocation())
            .skills(skills)
            .skillGroups(getSkillGroupNames(getSkillGroupsFromPersonal(personal)))
            .build();
    }
    
    
    public List<PersonalDocument> convertToListDocument(List<Personal> listPersonal) {
    	List<PersonalDocument> listDocument = new ArrayList<>();
    	for (Personal personal : listPersonal) {
    		listDocument.add(convertToDocument(personal));
    	}
    	return listDocument;
    }

    @Deprecated
    public PersonalDocument convertFromAddSkillToDto (AddSkillDto addSkillDto) {
    	PersonalDto personalDto = addSkillDto.getPersonalDto();
    	
        int exp = 0;
        if (!Objects.isNull(personalDto.getExperiencedAtBosch())) {
            try {
                int boschExp = Integer.parseInt(personalDto.getExperiencedAtBosch());
                exp += boschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Objects.isNull(personalDto.getExperiencedNonBosch())) {
            try {
                int nonBoschExp = Integer.parseInt(personalDto.getExperiencedNonBosch());
                exp += nonBoschExp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    	Team team = teamRepository.getById(personalDto.getTeam());
    	Level level = levelRepository.getById(personalDto.getLevelId());
    	Department department = departmentRepository.getById(personalDto.getDepartment());
    	
    	List<Skill> personalSkills = skillRepository.findAll();
    	List<String> skills = new ArrayList<>();
    	for (Skill ps : personalSkills) {
    		String skillName = ps.getName();
    		skills.add(skillName);
    	}
    	return PersonalDocument.builder()
    			.id(personalDto.getId())
    			.personalId(personalDto.getId())
    			.personalCode(personalDto.getCode())
    			.displayName(personalDto.getName())
    			.team(team.getName())
    			.level(level.getName())
    			.experience(exp)
    			.department(department.getName())
    			.location(personalDto.getLocation())
    			.skills(skills)
    			.skillGroups(personalDto.getSkillCluster())
    			.build();
    	
    }
    
    public Personal mapAddSkillDtoToEntity(AddSkillDto addSkillDto) {
        if (addSkillDto == null || addSkillDto.getPersonalDto() == null) {
            throw new SkillManagementException(MessageCode.VALIDATION_ERRORS.toString(),
                    messageSource.getMessage(MessageCode.VALIDATION_ERRORS.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }

    	Optional<User> userOpt = userRepository.findById(addSkillDto.getPersonalDto().getId());
    	if(!userOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
    	}
    	Optional<User> managerOpt = userRepository.findById(addSkillDto.getPersonalDto().getManager());
    	if(!managerOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
    	}
    	Optional<Department> departmentOpt = departmentRepository.findById(addSkillDto.getPersonalDto().getDepartment());
    	if(!departmentOpt.isPresent()) {
                throw new SkillManagementException(MessageCode.SKM_DEPARTMENT_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKM_DEPARTMENT_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, NOT_FOUND);
        	}
    	Optional<Level> levelOpt = levelRepository.findById(addSkillDto.getPersonalDto().getLevelId());
    	if(!levelOpt.isPresent()) {
                throw new SkillManagementException(MessageCode.LEVEL_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.LEVEL_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, NOT_FOUND);
        	}
    	Optional<Team> teamOpt = teamRepository.findById(addSkillDto.getPersonalDto().getTeam());
    	if (!teamOpt.isPresent()) {
            throw new SkillManagementException(MessageCode.SKM_TEAM_NOT_FOUND.toString(),
                    messageSource.getMessage(MessageCode.SKM_TEAM_NOT_FOUND.toString(), null,
                            LocaleContextHolder.getLocale()), null, NOT_FOUND);
        }
    	String skillCluster = String.join(",", addSkillDto.getSkillGroupIds());
    	int expAtBosh = 0;
    	if (null != addSkillDto.getPersonalDto() && null != addSkillDto.getPersonalDto().getJoinDate()) {
    		expAtBosh = LocalDate.now().getYear() - addSkillDto.getPersonalDto().getJoinDate().getYear();
        }

        // Experienced non Bosch
        if (addSkillDto.getPersonalDto().getExperiencedNonBoschType() != 1 &&
                addSkillDto.getPersonalDto().getExperiencedNonBoschType() != 12) {
            throw new SkillManagementException(MessageCode.EXPERIENCED_NON_BOSCH_TYPE_WRONG.toString(),
                    messageSource.getMessage(MessageCode.EXPERIENCED_NON_BOSCH_TYPE_WRONG.toString(), null,
                            LocaleContextHolder.getLocale()), null, HttpStatus.BAD_REQUEST);
        }
        int experiencedNonBoschType = addSkillDto.getPersonalDto().getExperiencedNonBoschType();
        int experiencedNonBosch;
        try {
            experiencedNonBosch = Integer.parseInt(addSkillDto.getPersonalDto().getExperiencedNonBosch());
            if(experiencedNonBosch < 0) {
				experiencedNonBosch = 0;
			}
        }  catch (Exception e) {
        	experiencedNonBosch = 0;
        }
        if (experiencedNonBoschType == 1) { // Month => Convert to Year
            experiencedNonBosch = experiencedNonBosch / 12;
        }
        PersonalDto personalDto = addSkillDto.getPersonalDto();
    	return Personal.builder()
    			.id(personalDto.getId())
    			.level(levelOpt.get())
    			.personalCode(personalDto.getCode())
    			.gender(personalDto.getGender())
    			.department(departmentOpt.get())
    			.title(personalDto.getTitle())
    			.team(teamOpt.get())
    			.location(personalDto.getLocation())
    			.joinDate(personalDto.getJoinDate())
    			.personalNumber(personalDto.getPersonalNumber())
    			.manager(managerOpt.get())
    			.experiencedNonBosch(String.valueOf(experiencedNonBosch))
    			.experiencedAtBosch(String.valueOf(expAtBosh))
    			.mainSkillCluster(skillCluster)
    			.updated(false)
    			.build();
    }
    public PersonalDto convertToNotEvaluateDTO(Personal personal) {
        User user = personal.getUser();
        Level level = personal.getLevel();
        Team team = personal.getTeam();
        return PersonalDto.builder()
                .id(personal.getId())
                .name(Objects.nonNull(user) ? user.getDisplayName() : null)
                .level(Objects.nonNull(level) ? level.getName() : null)
                .team(Objects.nonNull(team) ? team.getName() : null)
                .skillCluster(getSkillGroupNames(getSkillGroupsFromPersonal(personal)))
                .build();
    }
    
    public List<PersonalDto> convertToNotEvaluateDTOs(List<Personal> listPersonal) {
        List<PersonalDto> returnList = new ArrayList<>();
        for (Personal personal : listPersonal) {
            returnList.add(convertToNotEvaluateDTO(personal));
        }
        return returnList;
    }

    public List<SkillGroup> getSkillGroupsFromPersonal(Personal personal){
        List<SkillGroup> skillGroups = new ArrayList<>();
        List<PersonalSkillGroup> personalSkillGroups = personal.getPersonalSkillGroups();
        if(Objects.nonNull(personalSkillGroups)) {
            skillGroups = personalSkillGroups.stream()
                    .map(PersonalSkillGroup::getSkillGroup).collect(Collectors.toList());
        }
        return skillGroups;
    }

    public List<String> getSkillGroupNames(List<SkillGroup> skillGroups){
        return skillGroups.stream()
                .map(SkillGroup::getName)
                .collect(Collectors.toList());
    }

    private List<String> getSkillGroupIds(List<SkillGroup> skillGroups) {
        return skillGroups.stream()
                .map(SkillGroup::getId)
                .collect(Collectors.toList());
    }
    
    public PersonalDto convertToExcelDto(Personal personal) {
    	int exp = 0;
    	int boschExp = 0;
    	int nonBoschExp = 0;
    	
        if (org.apache.commons.lang3.StringUtils.isNotBlank(personal.getExperiencedAtBosch())) {
            try {
                boschExp = Integer.parseInt(personal.getExperiencedAtBosch());
                exp += boschExp;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(personal.getExperiencedNonBosch())) {
            try {
                nonBoschExp = Integer.parseInt(personal.getExperiencedNonBosch());
                exp += nonBoschExp;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        
        List<String> mainSkillCluster = personal.getPersonalSkillGroups().stream().map(personalSkillGroup ->
                personalSkillGroup.getSkillGroup().getName()).collect(Collectors.toList());
        
        List<String> skillList = personalSkillRepository.findSkillByPesonalIdAndNotEqualLevel(personal.getId(), Constants.DEFAULT_FLOAT_SKILL_LEVEL)
        	.stream().map(Skill::getName).collect(Collectors.toList());
        
        List<String> skillHighlightList = new ArrayList<>();
        if(!Objects.isNull(personal.getSkillHighlights())) {
        	personal.getSkillHighlights().forEach(highlight -> {
        		skillHighlightList.add(highlight.getPersonalSkill().getSkill().getName());
			});
        }
        User user = personal.getUser();
        Team team = personal.getTeam();
        String groupName, managerName;
        groupName = managerName = null;
        if(Objects.nonNull(team)){
            DepartmentGroup group = team.getDepartmentGroup();
            groupName = Objects.nonNull(group) ? group.getName() : null;

            User manager = team.getLineManager();
            managerName = Objects.nonNull(manager) ? manager.getDisplayName() : null;
        }
        Department department = personal.getDepartment();
        Level level = personal.getLevel();

        return PersonalDto.builder()
                .id(personal.getId())
                .name(Objects.nonNull(user) ? user.getDisplayName() : null)
                .code(personal.getPersonalCode())
                .departmentName(Objects.nonNull(department) ? department.getName() : null)
                .group(groupName)
    			.team(Objects.nonNull(team) ? team.getName() : null)
                .gender(Gender.getGenderByLabel(personal.getGender()).getValue())
                .email(Objects.nonNull(user) ? user.getEmail() : null)
                .level(Objects.nonNull(level) ? level.getName() : null)
                .title(personal.getTitle())
                .supervisorName(managerName)
                .experiencedAtBoschInt(boschExp)
                .experiencedNonBoschInt(nonBoschExp)
                .totalExperiencedInt(exp)
                .skillGroup(mainSkillCluster.size() == 0 ? null : String.join(", ", mainSkillCluster))
                .skill(skillList.size() == 0 ? null : String.join(", ", skillList))
                .joinDate(personal.getJoinDate())
                .location(personal.getLocation())
                .briefInfo(personal.getBriefInfo())
                .skillHighlights(skillHighlightList.size() == 0 ? null : String.join(", ", skillHighlightList))
                .build();
    }
}
