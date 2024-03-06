package com.bosch.eet.skill.management.usermanagement.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.common.ObjectMapperUtils;
import com.bosch.eet.skill.management.usermanagement.dto.ConfigurationCheckDto;
import com.bosch.eet.skill.management.usermanagement.dto.ConfigurationDto;
import com.bosch.eet.skill.management.usermanagement.entity.Configuration;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.entity.UserHasConfiguration;
import com.bosch.eet.skill.management.usermanagement.repo.ConfigurationRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserHasConfigurationRepository;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.ConfigurationService;

@Service
public class ConfigurationServiceImpl implements ConfigurationService{
	
	@Autowired
	private ConfigurationRepository repo;

	@Autowired
	private UserHasConfigurationRepository userHasConfigurationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<ConfigurationDto> findAllByUserId(final String userId) {
		final List<Configuration> entities = repo.findAllByUserId(userId);
		List<ConfigurationDto> dtos = ObjectMapperUtils.mapAll(entities, ConfigurationDto.class);
		
		for (ConfigurationDto configurationDto : dtos) {
			final Configuration entity = entities.stream().filter(dto -> dto.getId().equals(configurationDto.getId())).findFirst().get();
			setUserHasConfiguration(entity, configurationDto, userId);
		}
		return dtos;
	}

	@Override
	public void check(final ConfigurationCheckDto dto) {
		final String configurationId = dto.getConfigurationId();
		final String userId = dto.getUserId();
		
		Optional<Configuration> configurationOpt = repo.findById(configurationId);
		Optional<User> userOpt = userRepository.findById(userId);
		
		if (configurationOpt.isPresent() && userOpt.isPresent()) {
			UserHasConfiguration userHasConfiguration = new UserHasConfiguration();
			Configuration configuration = configurationOpt.get();
			User user = userOpt.get();
			
			// Update case
			UserHasConfiguration result = userHasConfigurationRepository.findByConfigurationIdAndUserId(configurationId, userId);
			if (result != null) {
				userHasConfiguration.setId(result.getId());
			}
			userHasConfiguration.setConfiguration(configuration);
			userHasConfiguration.setUser(user);
			userHasConfiguration.setIsChecked(dto.getIsChecked());
			
			userHasConfigurationRepository.save(userHasConfiguration);
		}
	}
	
	private void setUserHasConfiguration(final Configuration entity, ConfigurationDto configurationDto, final String userId) {
		final Optional<UserHasConfiguration> userHasConfigurationOpt = entity.getUserHasConfigurations()
				.stream().filter(dto -> dto.getUser().getId().equals(userId)).findFirst();
		
		// UserHasConfiguration is null, user don't have configuration
		if (userHasConfigurationOpt.isPresent()) {
			configurationDto.setIsChecked(userHasConfigurationOpt.get().getIsChecked());
		} else {
			configurationDto.setIsChecked(false);
		}
	}

}
