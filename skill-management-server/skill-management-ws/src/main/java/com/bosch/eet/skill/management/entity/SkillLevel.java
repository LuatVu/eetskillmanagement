package com.bosch.eet.skill.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="skill_level")
public class SkillLevel {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "level_id" , nullable=false, referencedColumnName = "id")
	private Level level;
	
	@ManyToOne
	@JoinColumn(name="skill_id", nullable=false, referencedColumnName = "id")
	private Skill skill;
	
	@ManyToOne
	@JoinColumn(name="skill_group_id", nullable=false, referencedColumnName = "id")
	private SkillGroup skillGroup;
	
	@Column(name="level_lable")
	private String levelLable;
	
}
