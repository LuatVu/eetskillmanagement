package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.Date;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`request_evaluation_detail`")
public class RequestEvaluationDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @ManyToOne
    @JoinColumn(name="skill_id", nullable=false)
    private Skill skill;
    
    @ManyToOne
    @JoinColumn(name="approver_id", nullable=false)
    private Personal approver;

    @Column(name = "current_level")
    private float currentLevel;

    @Column(name = "current_exp")
    private Integer currentExp;

    private String comment;
    
    @Column(name = "approver_comment")
    private String approverComment;

    @Column(name = "created_date")
    private Date createdDate;
    
    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "approved_date")
    private Date approvedDate;

    private String status;

    @ManyToOne
    private RequestEvaluation requestEvaluation;
}
