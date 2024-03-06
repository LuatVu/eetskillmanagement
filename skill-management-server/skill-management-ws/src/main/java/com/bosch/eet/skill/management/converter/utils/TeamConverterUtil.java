package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;

@Component
public final class TeamConverterUtil {

	private TeamConverterUtil() {
		// prevent instantiation
	}
	
    public static TeamDto convertToDTO(Team team) {
        TeamDto teamDto = TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .group(team.getDepartmentGroup() != null ? team.getDepartmentGroup().getName() : "")
                .build();

        if(team.getLineManager()!=null) {
			teamDto.setLineManager(UserDTO.builder()
			        .id(team.getLineManager().getId())
			        .displayName(team.getLineManager().getDisplayName())
			        .build());
		}
        return teamDto;
    }

    public static List<TeamDto> convertToDTOS(List<Team> teams) {
        List<TeamDto> teamDtos = new ArrayList<>();
        for (Team team : teams) {
            teamDtos.add(convertToDTO(team));
        }
        return teamDtos;
    }

}
