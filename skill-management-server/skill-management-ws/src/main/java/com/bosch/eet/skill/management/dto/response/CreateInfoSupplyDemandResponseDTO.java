package com.bosch.eet.skill.management.dto.response;

import java.util.List;

import com.bosch.eet.skill.management.dto.PicDto;
import com.bosch.eet.skill.management.dto.ProjectTypeDto;
import com.bosch.eet.skill.management.dto.SkillClusterDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInfoSupplyDemandResponseDTO {
    List<ProjectTypeDto> projects;
    List<SkillClusterDTO> skillClusters;
    List<ProjectTypeDto> levels;
    List<ProjectTypeDto> locations;
    List<PicDto> assignees;
}
