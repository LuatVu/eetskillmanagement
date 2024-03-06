package com.bosch.eet.skill.management.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;
    
    private String email;
    
    private String group;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("personal_number")
    private String personalNumber;

    private String code;

    private String title;

    private String skill;

    @JsonProperty("skill_group")
    private String skillGroup;

    private String picture;

    @JsonProperty("supervisor_name")
    private String supervisorName;

    @JsonProperty("team_id")
    private String teamId;

    @JsonProperty("department_name")
    private String departmentName;

    @JsonProperty("experienced_at_bosch")
    private String experiencedAtBosch;

    @JsonProperty("experienced_non_bosch")
    @Pattern(regexp = "^[\\d]{0,2}$", message = "must be unsigned number smaller than 100") // 0 -> 99
    private String experiencedNonBosch;

    @JsonProperty("experienced_non_bosch_type")
    private int experiencedNonBoschType; // 1 -> Month; 12 -> Year

    @JsonProperty("total_experienced")
    private String totalExperienced;

    private String level;

    @JsonProperty("level_id")
    private String levelId;

    private String team;
    
    private String department;
    
    private String manager;
    
    @JsonProperty("top_skills")
    private List<SkillDto> topSkills;

    private List<SkillDto> skills;
    
    @JsonProperty("skill_cluster_id")
    private List<String> skillClusterId;
    
    @JsonProperty("skill_cluster")
    private List<String> skillCluster;

    private List<PersonalProjectDto> projects;

    private List<CourseDto> courses;

    private LocalDate joinDate;

    private String gender;

    @JsonProperty("gender_code")
    private String genderCode;

    private String location;
    
    private Boolean isUpdated;
    
    @JsonProperty("brief_info")
    @Size(max = 250)
    private String briefInfo;
    
    private String skillHighlights;
    
    private int experiencedAtBoschInt;

    private int experiencedNonBoschInt;
    
    private int totalExperiencedInt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalDto that = (PersonalDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
