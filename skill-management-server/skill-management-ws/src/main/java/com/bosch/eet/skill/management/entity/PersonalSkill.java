package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
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
@Table(name = "`personal_skill`")
public class PersonalSkill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private float level;
    private Integer experience;

    @ManyToOne
    private Personal personal;

    @ManyToOne
    private Skill skill;
    
    @OneToMany(mappedBy = "personalSkill")
    private List<SkillHighlight> skillHighlights;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalSkill that = (PersonalSkill) o;
        return personal.getId().equals(that.personal.getId()) && skill.getId().equals(that.skill.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(personal, skill);
    }

    @PreRemove
    private void removePersonalSkillFromPersonal() {
        if(personal.getPersonalSkills() != null) {
            personal.getPersonalSkills().remove(this);
        }
    }
}
