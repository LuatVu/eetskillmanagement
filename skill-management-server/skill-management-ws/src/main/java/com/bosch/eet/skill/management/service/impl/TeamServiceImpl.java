package com.bosch.eet.skill.management.service.impl;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.converter.utils.TeamConverterUtil;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.repo.TeamRepository;
import com.bosch.eet.skill.management.service.TeamService;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    @Transactional
    public TeamDto updateTeam(TeamDto teamDto){
        try {
            Optional<Team> teamOpt = teamRepository.findById(teamDto.getId());
            Optional<User> userOpt = userRepository.findById(teamDto.getLineManager().getId());
            if(!teamOpt.isPresent()){
                throw new SkillManagementException(MessageCode.SKM_TEAM_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKM_TEAM_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }
            if(!userOpt.isPresent()){
                throw new SkillManagementException(MessageCode.SKM_USER_NOT_FOUND.toString(),
                        messageSource.getMessage(MessageCode.SKM_USER_NOT_FOUND.toString(), null,
                                LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
            }

            Team dbTeam = teamOpt.get();
            dbTeam.setLineManager(userOpt.get());
            return TeamConverterUtil.convertToDTO(dbTeam);
        }catch (Exception e){
            e.printStackTrace();
            throw new SkillManagementException(MessageCode.SKM_UPDATE_TEAM_MANAGER_FAIL.toString(),
                    messageSource.getMessage(MessageCode.SKM_UPDATE_TEAM_MANAGER_FAIL.toString(), null,
                            LocaleContextHolder.getLocale()), null, INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PersonalDto findLineMangerByTeamId(String teamId){
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if(!teamOpt.isPresent()) {
            throw new SkillManagementException(com.bosch.eet.skill.management.usermanagement.consts.MessageCode.TEAM_NOT_EXIST.toString(),
                    messageSource.getMessage(com.bosch.eet.skill.management.usermanagement.consts.MessageCode.TEAM_NOT_EXIST.toString(), null,
                            LocaleContextHolder.getLocale()),
                    null, NOT_FOUND);
        }
        Team team = teamOpt.get();
        if(team.getLineManager()!=null) {
            return PersonalDto.builder()
                    .id(team.getLineManager().getId())
                    .name(team.getLineManager().getDisplayName())
                    .build();
        } else {return null;}
    }

}
