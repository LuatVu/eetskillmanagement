package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.bosch.eet.skill.management.usermanagement.entity.User;

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
@Table(name = "`personal`")
public class Personal implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @PrimaryKeyJoinColumn
    private String id;
    
    @Column(name = "personal_number")
    private String personalNumber;

    @Column(name = "personal_code")
    private String personalCode;

    @Column(name = "title")
    private String title;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager")
    private User manager;

    @ManyToOne
    @JoinColumn(name = "team")
    private Team team;

    @Column(name = "experienced_at_bosch")
    private String experiencedAtBosch;

    @Column(name = "experienced_non_bosch")
    private String experiencedNonBosch;

    private String picture;

    private String gender;
    
    private String location;

    @Column(name = "join_date")
    private LocalDate joinDate;
    
    @Column(name = "updated", columnDefinition = "boolean default false")
    private Boolean updated;

    @JoinColumn(name = "level", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Level level;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "personal", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH})
    private Set<PersonalSkill> personalSkills;

    @OneToMany(mappedBy = "personal")
    private List<PersonalProject> personalProjects;

    @OneToMany(mappedBy = "personal")
    private List<PersonalCourse> personalCourse;

    @OneToMany(mappedBy = "personal")
    private List<PersonalSkillEvaluation> personalSkillEvaluations;
    
    @Column(name = "main_skill_cluster")
    private String mainSkillCluster;
    
    @Column(name = "brief_info")
    private String briefInfo;
    
    @OneToMany(mappedBy = "personal")
    private List<SkillHighlight> skillHighlights;

    @OneToMany(mappedBy = "personal", fetch = FetchType.EAGER)
    private List<PersonalSkillGroup> personalSkillGroups;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean deleted = false;
}
