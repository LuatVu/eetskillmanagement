package com.bosch.eet.skill.management.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "personal_skill_group")
public class PersonalSkillGroup {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	@ManyToOne
	private Personal personal;

	@ManyToOne
	private SkillGroup skillGroup;

	@PreRemove
	private void removePersonalSkillGroupFromPersonal() {
		if(personal.getPersonalSkillGroups() != null) {
			personal.getPersonalSkillGroups().remove(this);
		}
	}

}
