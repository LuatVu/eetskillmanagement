package com.bosch.eet.skill.management.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class LDAPDistributionListRequest extends GenericRequestDTO {
	private static final long serialVersionUID = 5454137984221567020L;
	
	private String city;
	private String country;
	private String displayName;
	private String email;
	private String phonenumber;
	private String name;
	private List<LDAPDistributionListRequest> childGroups;
}
