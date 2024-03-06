package com.bosch.eet.skill.management.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillTypeDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

}
