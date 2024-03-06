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
@Table(name = "`request_evaluation`")
public class RequestEvaluation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @ManyToOne
    @JoinColumn(name="requester_id", nullable=false)
    private Personal requester;
    
    @ManyToOne
    @JoinColumn(name="approver_id", nullable=false)
    private Personal approver;
    
    private String comment;

    @Column(name = "created_date")
    private Date createdDate;
    
    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "approved_date")
    private Date approvedDate;

    private String status;

    @OneToMany(mappedBy = "requestEvaluation", cascade=CascadeType.PERSIST)
    private List<RequestEvaluationDetail> requestEvaluationDetails;
}
