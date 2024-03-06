package com.bosch.eet.skill.management.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.PhaseDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.PhaseService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping
@RestController
@Slf4j
public class PhaseRest {

    @Autowired
    PhaseService phaseService;

    @GetMapping(value = Routes.URI_REST_PHASE_DROPDOWN)
    public GenericResponseDTO<List<PhaseDto>> getPhasesForDropdown() {
        return  GenericResponseDTO.<List<PhaseDto>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(phaseService.findAllForDropdown())
                .build();
    }
}
