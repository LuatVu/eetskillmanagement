package com.bosch.eet.skill.management.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGbDto {
	
	String id;

    String gbName;

    List<ProjectDto> projectDtoList;

    int headCounts;

    int vModelCount;

    Map<String, Integer> GbInfo;
}
