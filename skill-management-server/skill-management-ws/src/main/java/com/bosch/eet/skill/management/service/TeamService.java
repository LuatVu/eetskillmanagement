package com.bosch.eet.skill.management.service;

import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.TeamDto;

public interface TeamService {

    TeamDto updateTeam(TeamDto teamDto);

    PersonalDto findLineMangerByTeamId(String teamId);
}
