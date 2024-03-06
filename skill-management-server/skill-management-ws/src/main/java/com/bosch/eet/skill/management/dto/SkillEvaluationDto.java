package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillEvaluationDto implements Serializable {

    private String id;

    private String name;

    private String competency;

    private String requester;

    private String approver;

    private String status;

    private String level;

    @JsonProperty("old_level")
    private String oldLevel;

    @JsonProperty("new_level")
    private String newLevel;

    private String experience;

    @JsonProperty("old_experience")
    private String oldExperience;

    @JsonProperty("new_experience")
    private String newExperience;

    @JsonProperty("request_date")
    private String requestDate;

    @JsonProperty("approver_date")
    private String approverDate;
}
