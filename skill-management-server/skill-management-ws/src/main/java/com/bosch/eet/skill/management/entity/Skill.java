package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "`skill`")
public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private String description;
    private Integer sequence;
    private String status;
    @Column(name = "is_mandatory", columnDefinition = "boolean default false")
    private Boolean isMandatory;
    @Column(name = "is_required", columnDefinition = "boolean default false")
    private Boolean isRequired;

    @OneToMany(mappedBy = "skill")
    private List<PersonalSkill> personalSkill;

    @ManyToOne
    private SkillGroup skillGroup;

    @OneToMany(mappedBy = "skill")
    private List<ProjectSkillGroupSkill> projectSkillGroupSkills;

    @Column(name = "es_updated", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean esUpdated = false;

    public Skill(String name) {
        this.name = name;
    }
}
