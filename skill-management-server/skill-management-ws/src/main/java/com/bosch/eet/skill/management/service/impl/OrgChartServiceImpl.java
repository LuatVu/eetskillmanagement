package com.bosch.eet.skill.management.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.PageConfigConverterUtil;
import com.bosch.eet.skill.management.dto.PageConfigDTO;
import com.bosch.eet.skill.management.entity.PageConfig;
import com.bosch.eet.skill.management.repo.PageConfigRepository;
import com.bosch.eet.skill.management.service.OrgChartService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrgChartServiceImpl implements OrgChartService {
	
	@Autowired
	private PageConfigRepository pageConfigRepo;

	@Override
	public PageConfigDTO getPageConfigById(String id) {
		Optional<PageConfig> pageConfig = pageConfigRepo.findById(id);
		if (pageConfig.isPresent()) {
			PageConfig entity = pageConfig.get();
			return PageConfigConverterUtil.convertToDTO(entity);
		}
		return null;
	}

	@Override
	public void updatePageConfig(PageConfigDTO pageConfig) {
		PageConfig entity = PageConfigConverterUtil.convertToEntity(pageConfig);
		pageConfigRepo.save(entity);
	}

	@Override
	public void deletePageConfig(String id) {
		Optional<PageConfig> pageConfig = pageConfigRepo.findById(id);
		if (pageConfig.isPresent()) {
			PageConfig entity = pageConfig.get();
			pageConfigRepo.delete(entity);
		}
	}
}
