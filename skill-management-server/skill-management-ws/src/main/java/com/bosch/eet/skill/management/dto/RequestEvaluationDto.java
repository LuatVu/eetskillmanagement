package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestEvaluationDto implements Serializable {

    private String id;
    @NotNull(message = "The requester is required.")
    private String requester;
    @NotNull(message = "The approver is required.")
    private String approver;
    
    @JsonProperty("approver_id")
    private String approverId;

    private String status;
    
    private String comment;

    @JsonProperty("request_evaluation_details")
    private List<SkillDescriptionDTO> requestEvaluationDetails;
    
    @JsonProperty("created_date")
    private String createdDate;
    
    @JsonProperty("updated_date")
    private String updateDate;

    @JsonProperty("approved_date")
    private String approvedDate;
    
    private String competencyLeadEvaluateAll;
}
