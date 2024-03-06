package com.bosch.eet.skill.management.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.converter.SupplyDemandConverter;
import com.bosch.eet.skill.management.dto.PicDto;
import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.dto.SkillClusterDTO;
import com.bosch.eet.skill.management.dto.request.CreateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.request.UpdateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.response.ChangeHistoryResponseDTO;
import com.bosch.eet.skill.management.dto.response.CreateInfoSupplyDemandResponseDTO;
import com.bosch.eet.skill.management.dto.response.SupplyDemandResponseDTO;
import com.bosch.eet.skill.management.entity.HistorySupplyDemand;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.SkillGroup;
import com.bosch.eet.skill.management.entity.SupplyDemand;
import com.bosch.eet.skill.management.enums.Assignee;
import com.bosch.eet.skill.management.enums.Level;
import com.bosch.eet.skill.management.enums.Location;
import com.bosch.eet.skill.management.enums.Status;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.ResourceNotFoundException;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.facade.AuthenticationFacade;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.repo.HistorySupplyDemandRepository;
import com.bosch.eet.skill.management.repo.ProjectRepository;
import com.bosch.eet.skill.management.repo.SkillGroupRepository;
import com.bosch.eet.skill.management.repo.SupplyDemandRepository;
import com.bosch.eet.skill.management.service.ProjectService;
import com.bosch.eet.skill.management.service.SkillGroupService;
import com.bosch.eet.skill.management.service.SupplyDemandService;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.Group;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.service.GroupService;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplyDemandServiceImpl implements SupplyDemandService {

    SupplyDemandRepository supplyDemandRepository;

    ProjectService projectService;

    UserService userService;

    SkillGroupService skillGroupService;

    ProjectRepository projectRepository;

    SkillGroupRepository skillGroupRepository;

    HistorySupplyDemandRepository historySupplyDemandRepository;

    GroupService userGroupService;

    EmailService emailService;

    SupplyDemandConverter mapper;

    AuthenticationFacade authenticationFacade;

    String ADMIN_DEMAND_SUPPLY = "ADMIN_DEMAND_SUPPLY";

    /**
     * Get information to create supply demand .
     *
     * @return CreateInfoSupplyDemandResponseDTO
     */
    @Override
    public CreateInfoSupplyDemandResponseDTO getInfoToCreateDemand() {
        List<ProjectTypeDto> levelNames = getAllLevels();
        List<ProjectTypeDto> locationNames = getAllLocations();
//        List<ProjectTypeDto> assignToNames = this.getAllAssignor();

        List<ProjectTypeDto> projects = projectService.findAll();
        List<SkillClusterDTO> skillClusters = skillGroupService.findAllSkillGroupsWithSkills();
        List<PicDto> picDtos = getPicsDemand();

        return CreateInfoSupplyDemandResponseDTO.builder()
                .skillClusters(skillClusters)
                .projects(projects)
                .assignees(picDtos)
                .locations(locationNames)
                .levels(levelNames)
                .build();
    }

    /**
     * Create list supply demand by request, size = request's quantity
     *
     * @param request
     * @return List<SupplyDemandResponseDTO>
     */
    @Override
    public List<SupplyDemandResponseDTO> createSupplyDemand(CreateSupplyDemandRequestDTO request) {

        Project project = getProject(request.getProjectId());
        SkillGroup skillGroup = getSkillCluster(request.getSkillClusterId());

        UserDTO userDTO = authenticationFacade.getAuthenticationUser();
        final Long[] maxId = {supplyDemandRepository.getMaxIdDemand()};
        List<SupplyDemand> entities = Stream.generate(() -> request)
                .limit(request.getQuantity())
                .map(mapper::convertToSupplyDemandEntity)
                .peek(supplyDemand -> {
                    maxId[0] += 1;
                    supplyDemand.setProject(project);
                    supplyDemand.setSkillGroup(skillGroup);
                    supplyDemand.setCreatedByNtid(userDTO.getName());
                    supplyDemand.setCreatedByName(userDTO.getDisplayName());
                    supplyDemand.setSubId(maxId[0]);
                })
                .collect(Collectors.toList());
        return mapper.convertToSupplyDemandResponseDTOs(supplyDemandRepository.saveAll(entities));
    }

    /**
     * Get all supply demand
     *
     * @return List<SupplyDemandResponseDTO>.
     */
    @Override
    public List<SupplyDemandResponseDTO> getAllSupplyDemand() {
        List<SupplyDemand> supplyDemands = supplyDemandRepository.findAll();

        UserDTO userDTO = authenticationFacade.getAuthenticationUser();
        boolean hasTmPermission = checkTmDemandPermission();
        return mapper.convertToSupplyDemandResponseDTOs(supplyDemands).stream()
                .peek(i -> {
                    boolean isOwner = userDTO.getName().equals(i.getCreatedByNtid());
                    boolean canDelete = (hasTmPermission || isOwner) && Status.DRAFT.equals(i.getStatus());
                    boolean canUpdate = (hasTmPermission || isOwner)
                            && !Status.CANCELED.equals(i.getStatus())
                            && !Status.FIELD.equals(i.getStatus());
                    i.setCanUpdate(canUpdate);
                    i.setCanDelete(canDelete);
                })
                .collect(Collectors.toList());
    }

    /**
     * Update a Supply demand, send notification if match with condition
     *
     * @param request
     * @return SupplyDemandResponseDTO
     */
    @Override
    @Transactional
    public SupplyDemandResponseDTO updateASupplyDemand(UpdateSupplyDemandRequestDTO request, String clientHref) {
        SupplyDemand entity = supplyDemandRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Supply demand not found with id: " + request.getId()));

        UserDTO userDTO = authenticationFacade.getAuthenticationUser();
        boolean isOwner = userDTO.getName().equals(entity.getCreatedByNtid());
        catchExceptionForPermissionDemand(entity.getId(), entity.getStatus(), isOwner, true, false);

        //Send email to user(owner, old pic, new pic)
       sendEmailNotification(entity, request, userDTO, clientHref);

        if (request.getStatus() != null && request.getStatus() != entity.getStatus()) {
            HistorySupplyDemand historyEntity = mapper.convertToHistorySupplyEntity(request);
            historyEntity.setSupplyDemand(entity);
            historyEntity.setUpdatedByName(userDTO.getDisplayName());
            historyEntity.setUpdatedByNtid(userDTO.getName());
            historyEntity.setOldStatus(entity.getStatus());
            historyEntity.setNewStatus(request.getStatus());
            historySupplyDemandRepository.save(historyEntity);
        }

        mapper.convertToUpdatedEntity(entity, request);
        if (request.getProjectId() != null) {
            Project project = getProject(request.getProjectId());
            entity.setProject(project);
        }
        if (request.getSkillClusterId() != null) {
            SkillGroup skillGroup = getSkillCluster(request.getSkillClusterId());
            entity.setSkillGroup(skillGroup);
        }
        entity.setUpdatedByName(userDTO.getDisplayName());
        entity.setUpdatedByNtid(userDTO.getName());

        return mapper.convertToSupplyDemandResponseDTO(supplyDemandRepository.save(entity));
    }

    void sendEmailNotification(SupplyDemand entity, UpdateSupplyDemandRequestDTO request, UserDTO userDTO, String clientHref) {
        if (Objects.isNull(entity) || Objects.isNull(request)
                || Objects.isNull(request.getAssignNtId())
                || Objects.isNull(userDTO) || Status.DRAFT.equals(request.getStatus())) {
			return;
		}

        if (Objects.nonNull(request.getStatus()) && !request.getStatus().equals(entity.getStatus())
                || (!request.getAssignNtId().equals(entity.getAssignNtId()))
        ) {
            List<String> ntIdTemps = new ArrayList<>(Arrays.asList(request.getAssignNtId(), userDTO.getName()));
            if (!Status.DRAFT.equals(entity.getStatus()) && StringUtils.isNotBlank(entity.getAssignNtId())) {
                ntIdTemps.add(entity.getAssignNtId());
            }
            clientHref = removeOldIdPath(clientHref);
            Set<String> ntIds = new HashSet<>(ntIdTemps);
            List<User> users = userService.findByNameIn(ntIds);
            if (CollectionUtils.isEmpty(users)) {
				return;
			}
            String param = "?id=" + "DS-" + entity.getSubId();
            String preferenceLink = StringUtils.isBlank(clientHref) ? "https://www.bosch.com/" :
                    clientHref + param;

            users.forEach(user -> {
                if (StringUtils.isNotBlank(user.getEmail())) {
                    String ticketId = "DS-" + entity.getSubId();
                    emailService.mailToUserAssignedDemand(user.getEmail(), preferenceLink, ticketId);
                }

            });
        }

    }

	private String removeOldIdPath(String clientHref) {
		return clientHref.split("\\?id=")[0];
	}

    /**
     * Delete a Supply demand
     *
     * @param id: supplyDemandId
     * @return id: supplyDemandId
     */
    @Override
    public String deleteSupplyDemand(String id) {
        SupplyDemand entity = supplyDemandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demand not found with id: " + id));
        UserDTO userDTO = authenticationFacade.getAuthenticationUser();
        boolean isOwner = userDTO.getName().equals(entity.getCreatedByNtid());
        catchExceptionForPermissionDemand(entity.getId(), entity.getStatus(), isOwner, false, true);
        supplyDemandRepository.delete(entity);
        return id;
    }

    /**
     * Get all change history of a supply demand
     *
     * @param id: supplyDemandId
     * @return List<ChangeHistoryResponseDTO> responses
     */
    @Override
    public List<ChangeHistoryResponseDTO> getDemandChangeHistoryById(String id) throws IllegalAccessException {
        List<HistorySupplyDemand> histories = historySupplyDemandRepository.findBySupplyDemandIdOrderByUpdatedDate(id);
        return mapper.convertToChangeHistoryResDTOs(histories);
    }

    boolean checkTmDemandPermission() {
        Authentication auth = authenticationFacade.getAuthentication();
        Collection<? extends GrantedAuthority> permissions = auth.getAuthorities();
        if (!CollectionUtils.isEmpty(permissions)) {
            for (GrantedAuthority authority : permissions) {
                if (ADMIN_DEMAND_SUPPLY.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    void catchExceptionForPermissionDemand(String id, Status status, boolean isOwner, boolean isUpdate, boolean isDelete) {
        if (Status.CANCELED.equals(status) || Status.FIELD.equals(status)) {
            log.error("User attempted to update or delete Cancel/Field demand with ID {}.", id);
            throw new SkillManagementException(null,
                    "Cannot update or delete cancel/Field demand.",
                    null, HttpStatus.BAD_REQUEST);
        }
        if (isUpdate) {
            if (!isOwner && !checkTmDemandPermission()) {
                log.error("User doesn't have permission to update demand with id: " + id);
                throw new SkillManagementException(MessageCode.NOT_AUTHORIZATION.name(),
                        "User doesn't have permission to update demand",
                        null, HttpStatus.UNAUTHORIZED);
            }
        }
        if (isDelete) {
            if (!Status.DRAFT.equals(status)) {
                log.error("User attempted to delete non draft demand with ID {}.", id);
                throw new SkillManagementException(null,
                        "Cannot delete demand with status NON DRAFT.",
                        null, HttpStatus.BAD_REQUEST);
            }
            if (!checkTmDemandPermission() && !isOwner) {
                log.error("User doesn't have permission to delete demand with id: " + id);
                throw new SkillManagementException(MessageCode.NOT_AUTHORIZATION.name(),
                        "User doesn't have permission to delete demand",
                        null, HttpStatus.UNAUTHORIZED);
            }

        }
    }

    SkillGroup getSkillCluster(String skillClusterId) {
        return skillGroupRepository.findById(skillClusterId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill Cluster not found with id: " + skillClusterId));
    }

    Project getProject(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    //TODO: will update in future => reference to table instead of enum
    List<ProjectTypeDto> getAllAssignor() {
        return Arrays.stream(Assignee.values())
                .map(l -> new ProjectTypeDto(l.name(), l.getValue()))
                .collect(Collectors.toList());
    }

    //TODO: will update in future => reference to table instead of enum
    List<ProjectTypeDto> getAllLevels() {
        return Arrays.stream(Level.values())
                .map(l -> new ProjectTypeDto(l.name(), l.getLevel()))
                .collect(Collectors.toList());
    }

    //TODO: will update in future => reference to table instead of enum
    List<ProjectTypeDto> getAllLocations() {
        return Arrays.stream(Location.values())
                .map(l -> new ProjectTypeDto(l.name(), l.getLocationName()))
                .collect(Collectors.toList());
    }

    List<PicDto> getPicsDemand() {
        List<Group> userGroups = userGroupService.findGroupByStartPrefix("Demand_");
        if (CollectionUtils.isEmpty(userGroups)) {
			return Collections.emptyList();
		}
        return mapper.convertToPicDtos(userGroups);
    }
    
    @Override
    public SupplyDemandResponseDTO getSupplyDemandBySubId(int subId) {
    	SupplyDemand supplyDemand = supplyDemandRepository.findBySubId(Integer.toUnsignedLong(subId))
    			.orElseThrow(() -> new ResourceNotFoundException("Demand not found with subId: " + subId));
    	
    	UserDTO userDTO = authenticationFacade.getAuthenticationUser();
        boolean hasTmPermission = checkTmDemandPermission();
        
        SupplyDemandResponseDTO dto = mapper.convertToSupplyDemandResponseDTO(supplyDemand);
        boolean isOwner = userDTO.getName().equals(supplyDemand.getCreatedByNtid());
        boolean canDelete = (hasTmPermission || isOwner) && Status.DRAFT.equals(dto.getStatus());
        boolean canUpdate = (hasTmPermission || isOwner)
                && !Status.CANCELED.equals(dto.getStatus())
                && !Status.FIELD.equals(dto.getStatus());
        dto.setCanUpdate(canUpdate);
        dto.setCanDelete(canDelete);
    	
    	return dto;
	}

}
