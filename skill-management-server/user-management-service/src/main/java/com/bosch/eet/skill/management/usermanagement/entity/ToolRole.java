package com.bosch.eet.skill.management.usermanagement.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`tool_role`")
public class ToolRole implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	private String code;
	
	private String name;

	@Column(name = "status", insertable = false, updatable = true)
	private String status;
	
	//bi-directional many-to-one association to UserToolRole
	@OneToMany(mappedBy = "toolRole", fetch = FetchType.EAGER)
	private List<UserToolRole> userToolRoles;
}
