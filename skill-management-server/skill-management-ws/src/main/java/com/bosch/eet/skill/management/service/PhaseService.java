package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.entity.Phase;

public interface PhaseService {
    List<Phase> findAllByListDescriptionLike(List<String> descriptionList);

    List<PhaseDto> findAllForDropdown();
}
