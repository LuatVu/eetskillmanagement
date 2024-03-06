package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
@Table(name = "`project`")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    private String leader;

    @Column(name = "team_size")
    private String teamSize;

    private String challenge;

    private String description;

    private String status;

    private String customerGb;
    
    
    @Column(name = "technology_used")
    private String technologyUsed;

    @Column(name = "target_object")
    private String targetObject;

    @Column(name = "stack_holder")
    private String stackHolder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gb_unit", nullable=true)
    private GbUnit gbUnit;

    @Column(name = "reference_link")
    private String referenceLink;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="department", nullable=true)
    private Department department;

    @Column(name = "created_by", nullable = false, insertable = true, updatable = false)
    private String createdBy;

    @Column(name = "created_date", nullable = false, insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "modified_by", nullable = true, insertable = false, updatable = true)
    private String modifiedBy;

    @Column(name = "modified_date", nullable = true, insertable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    @Column(name = "top_project", columnDefinition = "boolean default false")
    private boolean isTopProject;

    @OneToMany(mappedBy = "project")
    private List<PersonalProject> personalProject;

    @OneToMany(mappedBy = "project")
    private List<SupplyDemand> supplyDemands;

    @ManyToOne
    private ProjectType projectType;

    @OneToMany(mappedBy = "project")
    private List<PhaseProject> phaseProjects;

    @OneToMany(mappedBy = "project")
    private List<ProjectSkillGroupSkill> projectSkillGroupSkills;
    
    //VOU6HC - Create Many to Many relationship with table project_skill_tags
    @OneToMany(mappedBy = "project")
    private Set<ProjectSkillTag> projectSkillTags;

    @Transient
    private List<Phase> phases;
    
    @ManyToOne
    @JoinColumn(name="customer_id", nullable=true)
    private Customer customer;
    
    @Column
    private String hightlight;
    
    @Column
    private String problemStatement;
    
    @Column
    private String solution;
    
    @Column
    private String benefits;

    @ManyToOne
    @JoinColumn(name = "project_scope_id", nullable=true)
    private ProjectScope projectScope;
    
}
