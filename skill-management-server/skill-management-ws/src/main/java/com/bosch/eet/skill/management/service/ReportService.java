package com.bosch.eet.skill.management.service;

import java.util.HashMap;
import java.util.Map;

import com.bosch.eet.skill.management.dto.ReportDto;

public interface ReportService {

    HashMap<String, Object> getFilter();

    ReportDto generateReport(Map<String, String> filterParams);
    ReportDto generateReportV2(Map<String, String> filterParams);
    ReportDto generateAssociateReport(Map<String, String> filterParams);
    ReportDto generateProjectReport(Map<String, String> filterParams);

}
