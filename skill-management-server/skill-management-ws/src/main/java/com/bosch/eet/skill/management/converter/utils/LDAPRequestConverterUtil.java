package com.bosch.eet.skill.management.converter.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bosch.eet.skill.management.dto.request.LDAPDistributionListRequest;
import com.bosch.eet.skill.management.dto.request.LDAPUserRequest;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;

@Component
public final class LDAPRequestConverterUtil {
	
	private LDAPRequestConverterUtil() {
		// prevent instantiation
	}
	
	// Converter for LDAPUser
	public static UserDTO convertToDTO(LDAPUserRequest request) {
		UserDTO output = new UserDTO();
		if (null != request) {
			output = new UserDTO();
			output.setName(request.getSAmAccountName());
			output.setDisplayName(request.getDisplayName());
			output.setEmail(request.getMail());
		}
		return output;
	}
	
	// Converter for LDAPDistributionList
	public static List<UserDTO> convertToDTO(LDAPDistributionListRequest request) {
		List<UserDTO> output = new ArrayList<>();
		if (null != request) {
			UserDTO userDTO = new UserDTO();
			userDTO.setName(request.getName());
			userDTO.setDisplayName(request.getDisplayName());
			userDTO.setEmail(request.getEmail());
			output.add(userDTO);
			if (!CollectionUtils.isEmpty(request.getChildGroups())) {
				List<UserDTO> userDTOs = request.getChildGroups().parallelStream().map(LDAPRequestConverterUtil::convertToDTO).flatMap(Collection::stream).collect(Collectors.toList());
				output.addAll(userDTOs);
			}
		}
		return output;
	}
}
