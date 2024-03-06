package com.bosch.eet.skill.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bosch.eet.skill.management.entity.GbUnit;
import com.bosch.eet.skill.management.repo.GbUnitRepository;
import com.bosch.eet.skill.management.service.GbUnitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GbUnitServiceImpl implements GbUnitService {
    private final GbUnitRepository gbUnitRepository;

    @Override
    public List<GbUnit> findAll() {
        return gbUnitRepository.findAll();
    }
}
