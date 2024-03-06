package com.bosch.eet.skill.management.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountAssociateResult {
    private Integer countAssociate;
    private Integer countFixedTerm;
    private Integer countInternal;
}
