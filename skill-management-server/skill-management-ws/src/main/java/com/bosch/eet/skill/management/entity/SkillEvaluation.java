package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name = "`skill_evaluation`")
public class SkillEvaluation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(name = "current_level")
    private Integer currentLevel;
    @Column(name = "target_level")
    private Integer targetLevel;
    @Column(name = "approve_date")
    private Date approveDate;
    private String comment;
    private Date date;
    private String status;
    private Integer experience;

    @ManyToOne
    @JoinColumn(name="skill_id", nullable=false)
    private Skill skill;

    @ManyToOne
    @JoinColumn(name="personal_id", nullable=false)
    private Personal personal;

    @ManyToOne
    @JoinColumn(name="approver", nullable=false)
    private Personal approver;

    @OneToMany(mappedBy = "skillEvaluation", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<PersonalSkillEvaluation> personalSkillEvaluations;
}
