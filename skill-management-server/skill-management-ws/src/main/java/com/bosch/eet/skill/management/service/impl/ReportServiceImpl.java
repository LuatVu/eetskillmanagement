package com.bosch.eet.skill.management.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.common.command.CommandApplyFilterFromPersonal;
import com.bosch.eet.skill.management.common.command.CommandApplyFilterFromProject;
import com.bosch.eet.skill.management.converter.utils.GbUnitConverterUtil;
import com.bosch.eet.skill.management.converter.utils.TeamConverterUtil;
import com.bosch.eet.skill.management.dto.GbUnitDto;
import com.bosch.eet.skill.management.dto.GroupProjectBySkillTag;
import com.bosch.eet.skill.management.dto.LevelDto;
import com.bosch.eet.skill.management.dto.ProjectDto;
import com.bosch.eet.skill.management.dto.ReportDto;
import com.bosch.eet.skill.management.dto.SkillDto;
import com.bosch.eet.skill.management.dto.SkillGroupDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Level;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.PersonalSkill;
import com.bosch.eet.skill.management.entity.PersonalSkillGroup;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.ProjectSkillTag;
import com.bosch.eet.skill.management.entity.Skill;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SkillTag;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.repo.GbUnitRepository;
import com.bosch.eet.skill.management.repo.LevelRepository;
import com.bosch.eet.skill.management.repo.PersonalRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SkillRepository;
import com.bosch.eet.skill.management.repo.TeamRepository;
import com.bosch.eet.skill.management.service.ReportService;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PersonalRepository personalRepository;

    private final SkillGroupRepository skillGroupRepository;

    private final SkillRepository skillRepository;

    private final LevelRepository levelRepository;

    private final GbUnitRepository gbUnitRepository;

    private final TeamRepository teamRepository;

    private final ProjectRepository projectRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public HashMap<String, Object> getFilter() {
        List<GbUnit> gbUnits = gbUnitRepository.findAll();
        List<GbUnitDto> gbUnitDtos = GbUnitConverterUtil.convertToDTOS(gbUnits);
        List<Team> teams = teamRepository.findByOrderByName();
        List<TeamDto> teamDtos = TeamConverterUtil.convertToDTOS(teams);
        HashMap<String, Object> result = new HashMap<>();
        result.put("gb_units", gbUnitDtos);
        result.put("teams", teamDtos);
        return result;
    }

    @Override
    public ReportDto generateReport(Map<String, String> filterParams) {
        List<TeamDto> teamDtos = new ArrayList<>();
        List<SkillDto> skillDtos = new ArrayList<>();
        List<SkillGroupDto> skillGroupDtos = new ArrayList<>();
        List<LevelDto> levelDtos = new ArrayList<>();
        List<GbUnitDto> gbUnitAssociates = new ArrayList<>();
        int associates = 0;
        int fixedTerms = 0;
        int internals = 0;
        String team = filterParams.get(Constants.TEAM);
        if (StringUtils.hasLength(team)) {
            for (String strTeam : team.split(Constants.COMMA)) {
                associates += personalRepository
                        .countAssociates(strTeam);
                fixedTerms += personalRepository
                        .countFixedTerms(strTeam);
                internals = associates - fixedTerms;

                Page<TeamDto> teamDtoPage = teamRepository
                        .findTopTeams(strTeam, PageRequest.of(0, 5));
                teamDtos.addAll(teamDtoPage.getContent());
                Page<SkillDto> skillDtoPage = skillRepository
                        .findTopSkills(strTeam, PageRequest.of(0, 5));
                for (SkillDto skillDto : skillDtoPage.getContent()) {
                    if (skillDtos.size() < 5) {
                        skillDtos.add(skillDto);
                    }
                }
                Page<SkillGroupDto> skillGroupDtoPage = skillGroupRepository
                        .findTopSkillGroups(strTeam, PageRequest.of(0, 5));
                for (SkillGroupDto skillGroupDto : skillGroupDtoPage.getContent()) {
                    if (skillGroupDtos.size() < 5) {
                        skillGroupDtos.add(skillGroupDto);
                    }
                }
                levelDtos.addAll(levelRepository.findAllLevels(strTeam));
                Page<GbUnitDto> gbUnitDtoPage = gbUnitRepository
                        .findTopGbAssociates(strTeam, PageRequest.of(0, 5));
                for (GbUnitDto gbUnitDto : gbUnitDtoPage.getContent()) {
                    if (gbUnitAssociates.size() < 5) {
                        gbUnitAssociates.add(gbUnitDto);
                    }
                }
            }
        } else {
            associates = personalRepository
                    .countAssociates(null);
            fixedTerms = personalRepository
                    .countFixedTerms(null);
            internals = associates - fixedTerms;
            teamDtos = teamRepository
                    .findTopTeams(null, PageRequest.of(0, 5)).getContent();
            skillDtos = skillRepository
                    .findTopSkills(null, PageRequest.of(0, 5)).getContent();
            skillGroupDtos = skillGroupRepository
                    .findTopSkillGroups(null, PageRequest.of(0, 5)).getContent();
            levelDtos = levelRepository.findAllLevels(null);
        }

        List<GbUnitDto> gbUnitProjects = new ArrayList<>();
        List<ProjectDto> projectDtos = new ArrayList<>();
        Integer projects = 0;
        ProjectDto projectDoneDto = new ProjectDto();
        projectDoneDto.setStatus(Constants.DONE);
        ProjectDto projectNewDto = new ProjectDto();
        projectNewDto.setStatus(Constants.DEFAULT_PROJECT_STATUS);
        ProjectDto projectInProgressDto = new ProjectDto();
        projectInProgressDto.setStatus(Constants.INPROGRESS);
        String gbUnit = filterParams.get(Constants.GB_UNIT_FILTER);
        if (StringUtils.hasLength(gbUnit)) {
            for (String strGbUnit : gbUnit.split(Constants.COMMA)) {
                projects += projectRepository
                        .countProjects(strGbUnit);
                projectDoneDto.setProjects(projectRepository
                        .countDistinctByStatus(strGbUnit, Constants.DONE));
                projectNewDto.setProjects(projectRepository
                        .countDistinctByStatus(strGbUnit, Constants.DEFAULT_PROJECT_STATUS));
                projectInProgressDto.setProjects(projectRepository
                        .countDistinctByStatus(strGbUnit, Constants.INPROGRESS));
                Page<GbUnitDto> gbUnitDtoPage = gbUnitRepository
                        .findTopGbProjects(strGbUnit, PageRequest.of(0, 5));
                for (GbUnitDto gbUnitDto : gbUnitDtoPage.getContent()) {
                    if (gbUnitProjects.size() < 5) {
                        gbUnitProjects.add(gbUnitDto);
                    }
                }
            }
        } else {
            projects = projectRepository.countProjects(null);
            gbUnitAssociates = gbUnitRepository
                    .findTopGbAssociates(null, PageRequest.of(0, 5)).getContent();
            gbUnitProjects = gbUnitRepository
                    .findTopGbProjects(null, PageRequest.of(0, 5)).getContent();
            projectDoneDto.setProjects(projectRepository
                    .countDistinctByStatus(null, Constants.DONE));
            projectInProgressDto.setProjects(projectRepository
                    .countDistinctByStatus(null, Constants.INPROGRESS));
            projectNewDto.setProjects(projectRepository
                    .countDistinctByStatus(null, Constants.DEFAULT_PROJECT_STATUS));
        }
        projectDtos.add(projectNewDto);
        projectDtos.add(projectInProgressDto);
        projectDtos.add(projectDoneDto);

        return ReportDto.builder()
                .associates(associates)
                .internals(internals)
                .fixedTerms(fixedTerms)
                .associatesByTeam(teamDtos)
                .associatesBySkills(skillDtos)
                .associatesBySkillCluster(skillGroupDtos)
                .associatesByLevels(levelDtos)
                .associatesByGb(gbUnitAssociates)
                .projects(projects)
                .projectsByStatus(projectDtos)
                .projectsByGb(gbUnitProjects)
                .build();
    }

    @Override
    public ReportDto generateReportV2(Map<String, String> filterParams) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Long countAssociate = reportCountAssociate(builder, filterParams);
        Long countFixedTerm = reportCountFixedTerm(builder, filterParams);

        return ReportDto.builder()
                .associates(Math.toIntExact(countAssociate))
                .internals(Math.toIntExact(countAssociate - countFixedTerm))
                .fixedTerms(Math.toIntExact(countFixedTerm))
                .associatesByTeam(reportGroupAssociateByTeam(builder, filterParams))
                .associatesBySkills(reportGroupAssociateBySkill(builder, filterParams))
                .associatesBySkillCluster(reportGroupAssociateBySkillGroup(builder, filterParams))
                .associatesByLevels(reportGroupAssociateByLevel(builder, filterParams))
                .associatesByGb(reportGroupAssociateByProjectGb(builder, filterParams))
                .projects(Math.toIntExact(reportCountProject(builder, filterParams)))
                .projectsByStatus(reportGroupProjectByStatus(builder, filterParams))
                .projectsByGb(reportGroupProjectByProjectGb(builder, filterParams))
                .projectBySkillTags(reportGroupProjectBySkillTag(builder, filterParams))
                .build();
    }

    @Override
    public ReportDto generateAssociateReport(Map<String, String> filterParams) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Long countAssociate = reportCountAssociate(builder, filterParams);
        Long countFixedTerm = reportCountFixedTerm(builder, filterParams);
        Long countExternal = reportCountExternal(builder, filterParams);
        return ReportDto.builder()
                .associates(Math.toIntExact(countAssociate))
                .externals(Math.toIntExact(countExternal))
                .internals(Math.toIntExact(countAssociate - countFixedTerm - countExternal))
                .fixedTerms(Math.toIntExact(countFixedTerm))
                .associatesByTeam(reportGroupAssociateByTeam(builder, filterParams))
                .associatesBySkills(reportGroupAssociateBySkill(builder, filterParams))
                .associatesBySkillCluster(reportGroupAssociateBySkillGroup(builder, filterParams))
                .associatesByLevels(reportGroupAssociateByLevel(builder, filterParams))
                .associatesByGb(reportGroupAssociateByProjectGb(builder, filterParams))
                .build();
    }

	@Override
	public ReportDto generateProjectReport(Map<String, String> filterParams) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		return ReportDto.builder()
				.projects(Math.toIntExact(reportCountProject(builder, filterParams)))
				.projectsByStatus(reportGroupProjectByStatus(builder, filterParams))
				.projectsByGb(reportGroupProjectByProjectGb(builder, filterParams))
				.projectBySkillTags(reportGroupProjectBySkillTag(builder, filterParams))
				.build();
	}

    private Long reportCountAssociate(CriteriaBuilder builder, Map<String, String> filterParams) {
        // Count associate
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, User> userJoin = fromPersonal.join("user");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.notLike(userJoin.get("displayName"), "System%"));
        predicates.add(builder.notLike(userJoin.get("displayName"), "MS/EET%"));

        predicates.addAll(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .build()));

        query.select(builder.count(fromPersonal)).where(
                builder.and(predicates.toArray(new Predicate[0]))
        );

        return entityManager.createQuery(query).getSingleResult();
    }

    private Long reportCountFixedTerm(CriteriaBuilder builder, Map<String, String> filterParams) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Personal> fromPersonal = query.from(Personal.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.like(fromPersonal.get("title"), Constants.INTERNSHIP_TITLE));

        predicates.addAll(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .build()));

        query.select(builder.count(fromPersonal)).where(
                builder.and(predicates.toArray(new Predicate[0]))
        );
        return entityManager.createQuery(query).getSingleResult();
    }
    private Long reportCountExternal(CriteriaBuilder builder, Map<String, String> filterParams) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, User> userJoin = fromPersonal.join("user");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.like(userJoin.get("displayName"), "EXTERNAL%"));

        predicates.addAll(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .build()));

        query.select(builder.count(fromPersonal)).where(
                builder.and(predicates.toArray(new Predicate[0]))
        );
        return entityManager.createQuery(query).getSingleResult();
    }
    private List<TeamDto> reportGroupAssociateByTeam(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<TeamDto> associateByTeam = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, Team> teamJoin = fromPersonal.join("team");

        List<Predicate> predicates = new ArrayList<>(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .teamJoin(teamJoin)
                .build()));

        query.multiselect(teamJoin.get("name"), builder.count(fromPersonal.get("id")))
                .groupBy(teamJoin.get("name"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = entityManager.createQuery(query).getResultList();
        Comparator<Object[]> nameComparator = Comparator.comparing(obj -> (String) obj[0]);

     // Sort the results list using the custom comparator
        results.sort(nameComparator);
        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                TeamDto teamDto = TeamDto.builder()
                        .name(objectMapper.convertValue(teamObjArr[0], String.class))
                        .associates(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                associateByTeam.add(teamDto);
            }
        }

        return associateByTeam;
    }

    private List<SkillGroupDto> reportGroupAssociateBySkillGroup(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<SkillGroupDto> associateBySkillGroup = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, PersonalSkillGroup> personalSkillGroupJoin = fromPersonal.join("personalSkillGroups");
        Join<PersonalSkillGroup, SkillGroup> skillGroupJoin = personalSkillGroupJoin.join("skillGroup");

        List<Predicate> predicates = new ArrayList<>(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .build()));

        query.multiselect(skillGroupJoin.get("name"), builder.count(fromPersonal.get("id")))
                .groupBy(skillGroupJoin.get("name"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                SkillGroupDto skillGroupDto = SkillGroupDto.builder()
                        .name(objectMapper.convertValue(teamObjArr[0], String.class))
                        .associates(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                associateBySkillGroup.add(skillGroupDto);
            }
        }

        return associateBySkillGroup;
    }

    private List<LevelDto> reportGroupAssociateByLevel(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<LevelDto> associateByLevel = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, Level> levelJoin = fromPersonal.join("level");

        List<Predicate> predicates = new ArrayList<>(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .build()));

        query.multiselect(levelJoin.get("name"), builder.count(fromPersonal.get("id")))
                .groupBy(levelJoin.get("name"))
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(builder.asc(levelJoin.get("name")));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                LevelDto levelDto = LevelDto.builder()
                        .name(objectMapper.convertValue(teamObjArr[0], String.class))
                        .associates(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                associateByLevel.add(levelDto);
            }
        }

        return associateByLevel;
    }

    private List<SkillDto> reportGroupAssociateBySkill(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<SkillDto> associateBySkill = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, PersonalSkill> personalSkillJoin = fromPersonal.join("personalSkills");
        Join<PersonalSkill, Skill> skillJoin = personalSkillJoin.join("skill");

        List<Predicate> predicates = new ArrayList<>(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .build()));

        query.multiselect(skillJoin.get("name"), builder.count(fromPersonal.get("id")))
                .groupBy(skillJoin.get("name"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = results = entityManager
                .createQuery(query)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                SkillDto skillDto = SkillDto.builder()
                        .name(objectMapper.convertValue(teamObjArr[0], String.class))
                        .associates(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                associateBySkill.add(skillDto);
            }
        }

        return associateBySkill;
    }

    private List<GbUnitDto> reportGroupAssociateByProjectGb(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<GbUnitDto> associateByProjectGb = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Personal> fromPersonal = query.from(Personal.class);
        Join<Personal, PersonalProject> personalProjectJoin = fromPersonal.join("personalProjects");
        Join<PersonalProject, Project> projectJoin = personalProjectJoin.join("project");
        Join<Project, GbUnit> gbUnitJoin = projectJoin.join("gbUnit");

        List<Predicate> predicates = new ArrayList<>(applyFilterFromPersonal(CommandApplyFilterFromPersonal.builder()
                .filterParams(filterParams)
                .fromPersonal(fromPersonal)
                .personalProjectJoin(personalProjectJoin)
                .projectJoin(projectJoin)
                .gbUnitJoin(gbUnitJoin)
                .build()));

        query.multiselect(gbUnitJoin.get("name"), builder.count(fromPersonal.get("id")))
                .groupBy(gbUnitJoin.get("name"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                GbUnitDto gbUnitDto = GbUnitDto.builder()
                        .name(objectMapper.convertValue(teamObjArr[0], String.class))
                        .associates(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                associateByProjectGb.add(gbUnitDto);
            }
        }

        return associateByProjectGb;
    }

    private Long reportCountProject(CriteriaBuilder builder, Map<String, String> filterParams) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Project> fromProject = query.from(Project.class);
        query.select(builder.count(fromProject));

        List<Predicate> predicates = new ArrayList<>(applyFilterFromProject(CommandApplyFilterFromProject.builder()
                .filterParams(filterParams)
                .fromProject(fromProject)
                .build()));

        query.select(builder.count(fromProject.get("id")))
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getSingleResult();
    }

    private List<GbUnitDto> reportGroupProjectByProjectGb(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<GbUnitDto> projectByProjectGb = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Project> fromProject = query.from(Project.class);
        Join<Project, GbUnit> gbUnitJoin = fromProject.join("gbUnit");

        List<Predicate> predicates = new ArrayList<>(applyFilterFromProject(CommandApplyFilterFromProject.builder()
                .filterParams(filterParams)
                .fromProject(fromProject)
                .gbUnitJoin(gbUnitJoin)
                .build()));

        query.multiselect(gbUnitJoin.get("name"), builder.count(fromProject.get("id")))
                .groupBy(gbUnitJoin.get("name"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                GbUnitDto gbUnitDto = GbUnitDto.builder()
                        .name(objectMapper.convertValue(teamObjArr[0], String.class))
                        .projects(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                projectByProjectGb.add(gbUnitDto);
            }
        }

        return projectByProjectGb;
    }

    private List<ProjectDto> reportGroupProjectByStatus(CriteriaBuilder builder, Map<String, String> filterParams) {
        List<ProjectDto> projectByStatus = new ArrayList<>();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
        Root<Project> fromProject = query.from(Project.class);

        List<Predicate> predicates = new ArrayList<>(applyFilterFromProject(CommandApplyFilterFromProject.builder()
                .filterParams(filterParams)
                .fromProject(fromProject)
                .build()));

        query.multiselect(fromProject.get("status"), builder.count(fromProject.get("id")))
                .groupBy(fromProject.get("status"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        if (CollectionUtils.isNotEmpty(results)) {
            for (Object[] teamObjArr : results) {
                String status = objectMapper.convertValue(teamObjArr[0], String.class);
                ProjectDto projectDto = ProjectDto.builder()
                        .status(org.apache.commons.lang3.StringUtils.isNotBlank(status) ? status : Constants.INPROGRESS)
                        .projects(objectMapper.convertValue(teamObjArr[1], Integer.class))
                        .build();
                projectByStatus.add(projectDto);
            }
        }

        return projectByStatus;
    }
    
	private List<GroupProjectBySkillTag> reportGroupProjectBySkillTag(CriteriaBuilder builder,
			Map<String, String> filterParams) {
		List<GroupProjectBySkillTag> projectBySkillTag = new ArrayList<>();
		CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
		Root<Project> fromProject = query.from(Project.class);
		Join<Project, ProjectSkillTag> joinProjectProjectSkillTag = fromProject.joinSet("projectSkillTags");
		Join<ProjectSkillTag, SkillTag> joinSkillTag = joinProjectProjectSkillTag.join("skillTag");

		List<Predicate> predicates = new ArrayList<>(applyFilterFromProject(
				CommandApplyFilterFromProject.builder().filterParams(filterParams).fromProject(fromProject).build()));
		Path<String> skillTagName = joinSkillTag.get("name");
		query.multiselect(skillTagName, builder.count(fromProject.get("id"))).groupBy(skillTagName)
				.orderBy(builder.desc(builder.count(fromProject.get("id"))))
				.where(predicates.toArray(new Predicate[0]));

		List<Object[]> results = entityManager.createQuery(query).getResultList();

		if (CollectionUtils.isNotEmpty(results)) {
			for (Object[] teamObjArr : results) {
				String status = objectMapper.convertValue(teamObjArr[0], String.class);
				GroupProjectBySkillTag projectDto = GroupProjectBySkillTag.builder().skillTagName(status)
						.count(objectMapper.convertValue(teamObjArr[1], Long.class)).build();
				projectBySkillTag.add(projectDto);
			}
		}

		return projectBySkillTag;
	}

    private List<Predicate> applyFilterFromPersonal(final CommandApplyFilterFromPersonal commandApplyFilterFromPersonal) {
        if (commandApplyFilterFromPersonal.getFromPersonal() == null) {
            return new ArrayList<>();
        }

        Root<Personal> fromPersonal = commandApplyFilterFromPersonal.getFromPersonal();
        Join<Personal, Team> teamJoin = commandApplyFilterFromPersonal.getTeamJoin();
        Map<String, String> filterParams = commandApplyFilterFromPersonal.getFilterParams();
        Join<Personal, PersonalProject> personalProjectJoin = commandApplyFilterFromPersonal.getPersonalProjectJoin();
        Join<PersonalProject, Project> projectJoin = commandApplyFilterFromPersonal.getProjectJoin();
        Join<Project, GbUnit> gbUnitJoin = commandApplyFilterFromPersonal.getGbUnitJoin();
        List<Predicate> predicates = new ArrayList<>();

        String teamsFilter = filterParams.get(Constants.TEAM);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(teamsFilter)) {
            List<String> listTeamsFilter = new ArrayList<>(Arrays.asList(teamsFilter.split(Constants.COMMA)));
            if (teamJoin == null) {
                teamJoin = fromPersonal.join("team");
            }
            predicates.add(teamJoin.get("name").in(listTeamsFilter));
        }

        String projectGbsFilter = filterParams.get(Constants.GB_UNIT_FILTER);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(projectGbsFilter)) {
            List<String> listProjectGbsFilter = new ArrayList<>(Arrays.asList(projectGbsFilter.split(Constants.COMMA)));
            if (personalProjectJoin == null) {
                personalProjectJoin = fromPersonal.join("personalProjects");
            }

            if (projectJoin == null) {
                projectJoin = personalProjectJoin.join("project");
            }

            if (gbUnitJoin == null) {
                gbUnitJoin = projectJoin.join("gbUnit");
            }

            predicates.add(gbUnitJoin.get("name").in(listProjectGbsFilter));
        }

        predicates.add(fromPersonal.get("deleted").in(false));
        predicates.add(fromPersonal.get("department").get("name").in(Constants.MS_EET));
        return predicates;
    }

    private List<Predicate> applyFilterFromProject(final CommandApplyFilterFromProject commandApplyFilterFromProject) {
        if (commandApplyFilterFromProject.getFromProject() == null) {
            return new ArrayList<>();
        }

        List<Predicate> predicates = new ArrayList<>();
        Root<Project> fromProject = commandApplyFilterFromProject.getFromProject();
        Map<String, String> filterParams = commandApplyFilterFromProject.getFilterParams();
        Join<Project, PersonalProject> personalProjectJoin = commandApplyFilterFromProject.getPersonalProjectJoin();
        Join<PersonalProject, Personal> personalJoin = commandApplyFilterFromProject.getPersonalJoin();
        Join<Personal, Team> teamJoin = commandApplyFilterFromProject.getTeamJoin();
        Join<Project, GbUnit> gbUnitJoin = commandApplyFilterFromProject.getGbUnitJoin();

        String teamsFilter = filterParams.get(Constants.TEAM);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(teamsFilter)) {
            if (personalProjectJoin == null) {
                personalProjectJoin = fromProject.join("personalProject");
            }

            if (personalJoin == null) {
                personalJoin = personalProjectJoin.join("personal");
            }

            if (teamJoin == null) {
                teamJoin = personalJoin.join("team");
            }

            List<String> listTeamsFilter = new ArrayList<>(Arrays.asList(teamsFilter.split(Constants.COMMA)));
            predicates.add(teamJoin.get("name").in(listTeamsFilter));
        }

        String projectGbsFilter = filterParams.get(Constants.GB_UNIT_FILTER);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(projectGbsFilter)) {
            if (gbUnitJoin == null) {
                gbUnitJoin = fromProject.join("gbUnit");
            }
            List<String> listProjectGbsFilter = new ArrayList<>(Arrays.asList(projectGbsFilter.split(Constants.COMMA)));
            predicates.add(gbUnitJoin.get("name").in(listProjectGbsFilter));
        }

        String typeFilter = filterParams.get(Constants.PROJECT_TYPE);
        if (org.apache.commons.lang3.StringUtils.isBlank(typeFilter)) {
            typeFilter = Constants.BOSCH;
        }
        List<String> listTypeFilter = new ArrayList<>(Arrays.asList(typeFilter.split(Constants.COMMA)));
        predicates.add(fromProject.get("projectType").get("name").in(listTypeFilter));

        return predicates;
    }
}
