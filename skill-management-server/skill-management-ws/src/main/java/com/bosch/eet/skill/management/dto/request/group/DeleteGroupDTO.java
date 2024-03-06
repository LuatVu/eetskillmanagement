package com.bosch.eet.skill.management.dto.request.group;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteGroupDTO implements Serializable {

    @NotBlank
    private String groupId;
}
