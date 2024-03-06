package com.bosch.eet.skill.management.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.converter.utils.PhaseConverterUtil;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.repo.PhaseRepository;
import com.bosch.eet.skill.management.service.PhaseService;
import com.bosch.eet.skill.management.specification.PhaseSpecification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PhaseServiceImpl implements PhaseService {

    private final PhaseRepository phaseRepository;

    @Override
    public List<Phase> findAllByListDescriptionLike(List<String> descriptionList) {
        Map<String, String> query = new HashMap<>();
        query.put("list_descriptions", String.join(",", descriptionList));
        Specification<Phase> phaseSpecification = PhaseSpecification.search(query);
        return phaseRepository.findAll(phaseSpecification);
    }

    @Override
    public List<PhaseDto> findAllForDropdown(){
        List<Phase> phaseList = phaseRepository.findAll(Sort.by(Sort.Direction.ASC, "description"));
        return phaseList.stream().map(PhaseConverterUtil::convertToDtoWithoutProject).collect(Collectors.toList());
    }

}
