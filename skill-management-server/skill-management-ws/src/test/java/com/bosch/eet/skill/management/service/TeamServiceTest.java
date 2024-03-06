package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bosch.eet.skill.management.SkillManagementWsApplication;
import com.bosch.eet.skill.management.dto.PersonalDto;
import com.bosch.eet.skill.management.dto.TeamDto;
import com.bosch.eet.skill.management.entity.DepartmentGroup;
import com.bosch.eet.skill.management.entity.Team;
import com.bosch.eet.skill.management.repo.TeamRepository;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SkillManagementWsApplication.class)
public class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Update team line manager")
    public void testUpdateTeam() {
        UserDTO userDTO = UserDTO.builder().id("testNewLineManagerId").build();
        TeamDto teamDto = TeamDto.builder().id("testTeamId").lineManager(userDTO).build();

        User oldLineManager = User.builder().id("testOldLineManagerId").build();
        DepartmentGroup departmentGroup = DepartmentGroup.builder().id("testGroup").build();

        Team team = Team.builder().id("testTeamId").name("testTeam").departmentGroup(departmentGroup).lineManager(oldLineManager).build();
        Optional<Team> teamOpt = Optional.of(team);

        User newLineManager = User.builder().id("testNewLineManagerId").build();
        Optional<User> newLineManagerOpt = Optional.of(newLineManager);

        when(teamRepository.findById(teamDto.getId())).thenReturn(teamOpt);
        when(userRepository.findById(teamDto.getLineManager().getId())).thenReturn(newLineManagerOpt);
        assertThat("testNewLineManagerId").isEqualTo(teamService.updateTeam(teamDto).getLineManager().getId());
    }

    @Test
    @DisplayName("Find line manger by teamId")
    public void testFindLineMangerByTeamId() {
        User lineManagerUser = User.builder().id("testLineManagerId").displayName("testDisplayName").build();

        Team team = Team.builder().id("testTeamId").name("testTeam").lineManager(lineManagerUser).build();
        Optional<Team> teamOpt = Optional.of(team);

        when(teamRepository.findById(team.getId())).thenReturn(teamOpt);

        PersonalDto result = teamService.findLineMangerByTeamId("testTeamId");
        assertThat("testDisplayName").isEqualTo(result.getName());
        assertThat("testLineManagerId").isEqualTo(result.getId());
        }
}