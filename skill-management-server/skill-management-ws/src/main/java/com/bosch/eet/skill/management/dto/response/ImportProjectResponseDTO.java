package com.bosch.eet.skill.management.dto.response;

import java.util.List;

import com.bosch.eet.skill.management.dto.ProjectDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportProjectResponseDTO {
    
    @JsonProperty("existed_project_list")
    private List<ProjectDto> existedProjectList;
}
