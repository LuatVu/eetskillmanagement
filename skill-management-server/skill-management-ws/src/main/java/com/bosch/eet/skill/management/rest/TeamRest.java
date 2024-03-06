package com.bosch.eet.skill.management.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.TeamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamRest {

    private final TeamService teamService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping(value = Routes.URI_REST_TEAM_ID)
    public GenericResponseDTO<TeamDto> updateTeam(@PathVariable(name = "id") String teamId,
                                                                        @RequestBody TeamDto teamDto) {
        try {
            TeamDto res = teamService.updateTeam(teamDto);
            return GenericResponseDTO.<TeamDto>builder()
                    .code(MessageCode.SUCCESS.toString())
                    .data(res)
                    .build();
        } catch (Exception e) {
            log.error(messageSource.getMessage(MessageCode.	SKM_UPDATE_TEAM_MANAGER_FAIL.toString(),
                    null, LocaleContextHolder.getLocale()));
            return GenericResponseDTO.<TeamDto>builder()
                    .code(MessageCode.	SKM_UPDATE_TEAM_MANAGER_FAIL.toString())
                    .build();
        }
    }

    @GetMapping(value = Routes.URI_REST_TEAM_LINE_MANAGER)
    public GenericResponseDTO<PersonalDto> getLineManager(@PathVariable(name = "id") String teamId) {
        return GenericResponseDTO.<PersonalDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(teamService.findLineMangerByTeamId(teamId))
                .build();
    }
}
