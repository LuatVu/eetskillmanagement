package com.bosch.eet.skill.management.service;

import com.bosch.eet.skill.management.dto.PageConfigDTO;

public interface OrgChartService {
	
	PageConfigDTO getPageConfigById(String id);
	
	void updatePageConfig(PageConfigDTO pageConfig);
	
	void deletePageConfig(String id);
}
