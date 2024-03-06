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
@Table(name = "`skill_competency_lead`")
public class SkillCompetencyLead implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name="personal_id", nullable=false, referencedColumnName = "id")
    private Personal personal;
    
    @ManyToOne
    @JoinColumn(name="skill_id", referencedColumnName = "id")
    private Skill skill;
    
    @ManyToOne
    @JoinColumn(name="skill_group_id", nullable=false, referencedColumnName = "id")
    private SkillGroup skillGroup;

    private String description;
    
    public SkillCompetencyLead(Personal personal) {
        this.personal = personal;
    }

    public SkillCompetencyLead(Personal personal, SkillGroup skillGroup) {
        this.personal = personal;
        this.skillGroup = skillGroup;
    }
    
}
