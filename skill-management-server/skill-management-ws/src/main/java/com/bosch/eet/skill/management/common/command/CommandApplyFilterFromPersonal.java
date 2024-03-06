package com.bosch.eet.skill.management.common.command;

import java.util.Map;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.PersonalProject;
import com.bosch.eet.skill.management.entity.Project;
import com.bosch.eet.skill.management.entity.Team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandApplyFilterFromPersonal {
    private Map<String, String> filterParams;
    private Root<Personal> fromPersonal;
    private Join<Personal, Team> teamJoin;
    private Join<Personal, PersonalProject> personalProjectJoin;
    private Join<PersonalProject, Project> projectJoin;
    private Join<Project, GbUnit> gbUnitJoin;
}
