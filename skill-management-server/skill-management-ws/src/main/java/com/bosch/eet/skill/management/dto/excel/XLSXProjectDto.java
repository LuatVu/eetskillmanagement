package com.bosch.eet.skill.management.dto.excel;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XLSXProjectDto {

	private String projectName;
	private Date startDate;
	private Date endDate;
	private String leader;
	private Double teamSize;
	private String challenge;
	private String status;
	private String projectDescription;
	private String projectType;
	private String stackHolder;
	private String gbUnitGroup;
	private String projectObjective;
	private String documentLink;
	private String technologiesUsed;
	private String createBy;
	private String member;
	private String vModelPhases;
	private String customerGb;
}
