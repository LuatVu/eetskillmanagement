package com.bosch.eet.skill.management.usermanagement.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
@Table(name = "user_tool_role")
public class UserToolRole implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	//bi-directional many-to-one association to User
	@ManyToOne
	private User user;

	//bi-directional many-to-one association to ToolRole
	@ManyToOne
	private ToolRole toolRole;
}
