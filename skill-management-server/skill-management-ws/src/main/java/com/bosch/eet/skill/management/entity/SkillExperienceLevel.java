package com.bosch.eet.skill.management.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "`skill_experience_level`")
public class SkillExperienceLevel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name="level_id", nullable=false, referencedColumnName = "id")
    private Level level;
    @ManyToOne
    @JoinColumn(name="skill_id", nullable=false, referencedColumnName = "id")
    private Skill skill;
    @ManyToOne
    @JoinColumn(name="skill_group_id", nullable=false, referencedColumnName = "id")
    private SkillGroup skillGroup;

    public SkillExperienceLevel(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
