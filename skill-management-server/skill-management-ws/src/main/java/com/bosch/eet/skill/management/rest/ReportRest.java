package com.bosch.eet.skill.management.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.ReportDto;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ReportRest {

    @Autowired
    private ReportService reportService;

    @GetMapping(value = Routes.URI_REST_REPORT_FILTER)
    public GenericResponseDTO<HashMap<String, Object>> getReportFilter() {
        return GenericResponseDTO.<HashMap<String, Object>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(reportService.getFilter())
                .timestamps(new Date())
                .build();
    }

    @GetMapping(value = Routes.URI_REST_REPORT)
    public GenericResponseDTO<ReportDto> generateReport(@RequestParam Map<String, String> filterParams) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("Start generating report");
        ReportDto reportDto = reportService.generateReportV2(filterParams);
        stopWatch.stop();
        log.info("Generate report took " + stopWatch.getTotalTimeMillis()
            + " ms ~= " + stopWatch.getTotalTimeSeconds() + " s ~=");
        return GenericResponseDTO.<ReportDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(reportDto)
                .timestamps(new Date())
                .build();
    }

    @GetMapping(value = Routes.URI_REST_REPORT_ASSOCIATE)
    public GenericResponseDTO<ReportDto> generateAssociateReport(@RequestParam Map<String, String> filterParams) {
        ReportDto reportDto = reportService.generateAssociateReport(filterParams);

        AtomicInteger totalByTeam = new AtomicInteger();
        reportDto.getAssociatesByTeam().forEach(item -> totalByTeam.addAndGet(item.getAssociates()));
        log.info("Total by team: {}", totalByTeam);

        AtomicInteger totalByLevel = new AtomicInteger();
        reportDto.getAssociatesByLevels().forEach(item -> totalByLevel.addAndGet(item.getAssociates()));
        log.info("Total by level: {}", totalByLevel);

        AtomicInteger totalBySkillGroup = new AtomicInteger();
        reportDto.getAssociatesBySkillCluster().forEach(item -> totalBySkillGroup.addAndGet(item.getAssociates()));
        log.info("Total by skillGroup: {}", totalBySkillGroup);

        return GenericResponseDTO.<ReportDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(reportDto)
                .timestamps(new Date())
                .build();
    }

    @GetMapping(value = Routes.URI_REST_REPORT_PROJECT)
    public GenericResponseDTO<ReportDto> generateProjectReport(@RequestParam Map<String, String> filterParams) {
        ReportDto reportDto = reportService.generateProjectReport(filterParams);

        AtomicInteger totalByStatus = new AtomicInteger();
        reportDto.getProjectsByStatus().forEach(item -> totalByStatus.addAndGet(item.getProjects()));
        log.info("Total by status: {}", totalByStatus);

        AtomicInteger totalByGb = new AtomicInteger();
        reportDto.getProjectsByGb().forEach(item -> totalByGb.addAndGet(item.getProjects()));
        log.info("Total by Gb: {}", totalByGb);

        AtomicInteger totalBySkilltag = new AtomicInteger();
        reportDto.getProjectBySkillTags().forEach(item -> totalBySkilltag.addAndGet(Math.toIntExact(item.getCount())));
        log.info("Total by skillGroup: {}", totalBySkilltag);

        return GenericResponseDTO.<ReportDto>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(reportDto)
                .timestamps(new Date())
                .build();
    }

}
