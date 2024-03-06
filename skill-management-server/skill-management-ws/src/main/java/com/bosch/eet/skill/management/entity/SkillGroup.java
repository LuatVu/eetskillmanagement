package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "`skill_group`")
public class SkillGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    @OneToMany(mappedBy = "skillGroup")
    private List<Skill> skills;

    @OneToMany(mappedBy = "skillGroup")
    private List<ProjectSkillGroupSkill> projectSkillGroupSkills;

    @OneToMany(mappedBy = "skillGroup")
    private List<SupplyDemand> supplyDemands;
    
    @ManyToOne
    @JoinColumn(name="skill_type_id")
    private SkillType skillType;

    @OneToMany(mappedBy = "skillGroup")
    private List<PersonalSkillGroup> personalSkillGroups;

    public SkillGroup(String name) {
        this.name = name;
    }
}
