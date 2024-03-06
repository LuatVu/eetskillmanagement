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
public class CommandApplyFilterFromProject {
    private Map<String, String> filterParams;
    private Root<Project> fromProject;
    private Join<Project, PersonalProject> personalProjectJoin;
    private Join<PersonalProject, Personal> personalJoin;
    private Join<Personal, Team> teamJoin;
    private Join<Project, GbUnit> gbUnitJoin;
}
