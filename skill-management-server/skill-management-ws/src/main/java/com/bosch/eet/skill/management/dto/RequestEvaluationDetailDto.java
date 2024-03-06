package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestEvaluationDetailDto implements Serializable {

    private String id;

    private String requester;
    
    @JsonProperty("request_evaluation_id")
    private String requestEvaluationId;

    @JsonProperty("skill_description")
    private SkillDescriptionDTO skillDescription;

    private String approver;

    private String status;
    
    private String comment;    
    
    @JsonProperty("approver_comment")
    private String approverComment;
    
    @JsonProperty("created_date")
    private String createdDate;
    
    @JsonProperty("update_date")
    private String updateDate;

    @JsonProperty("approved_date")
    private String approvedDate;

    private Boolean sentEmail;
}
