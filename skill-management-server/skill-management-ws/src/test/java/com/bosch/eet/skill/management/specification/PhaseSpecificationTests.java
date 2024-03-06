package com.bosch.eet.skill.management.specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import com.bosch.eet.skill.management.SkillManagementWsApplication;
import com.bosch.eet.skill.management.entity.Phase;
import com.bosch.eet.skill.management.repo.PhaseRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SkillManagementWsApplication.class)
public class PhaseSpecificationTests {

    @Autowired
    private PhaseRepository phaseRepository;

    @Test
    @DisplayName("search method with special phase insist comma: only special phase")
    @Transactional
    public void testSearchPhaseWithOnlySpecialPhase() throws Exception{
        Map<String, String> query = new HashMap<>();
        query.put("list_descriptions", "Product Management - Planning, Creation and Delivery");
        Specification<Phase> result = PhaseSpecification.search(query);
        List<Phase> phaseList = phaseRepository.findAll(result);

        assertThat(phaseList).hasSize(1);
        System.out.println("----------------");
        log.info("input: Product Management - Planning, Creation and Delivery");
        
        assertThat("prjPlanCreationDeli").isEqualTo(phaseList.get(0).getName());
        assertThat("Product Management - Planning, Creation and Delivery").isEqualTo( phaseList.get(0).getDescription());

        log.info("name: {} - description: {}",phaseList.get(0).getName(), phaseList.get(0).getDescription());
        System.out.println("----------------");
    }

    @Test
    @DisplayName("search method with special phase insist comma: special phase is at the beginning")
    @Transactional
    public void testSearchPhaseWithSpecialPhaseAtBeginning() throws Exception{
        Map<String, String> query = new HashMap<>();
        query.put("list_descriptions", "Product Management - Planning, Creation and Delivery,Generic review,System Architecture");
        Specification<Phase> result = PhaseSpecification.search(query);

        List<Phase> phaseList = phaseRepository.findAll(result);
        Map<String, Phase> resultMap = new HashMap<>();
        phaseList.forEach(phase -> resultMap.put(phase.getDescription(), phase));

        assertThat(phaseList).hasSize(3);
        System.out.println("----------------");
        log.info("input: Product Management - Planning, Creation and Delivery,Generic review,System Architecture");

        Phase result1 = resultMap.get("Product Management - Planning, Creation and Delivery");
        assertNotNull(result1);
        assertThat("prjPlanCreationDeli").isEqualTo(result1.getName());
        log.info("name: {} - description: {}",result1.getName(), result1.getDescription());

        Phase result2 = resultMap.get("Generic review");
        assertNotNull(result2);
        assertThat("genericReview").isEqualTo(result2.getName());
        log.info("name: {} - description: {}",result2.getName(), result2.getDescription());

        Phase result3 = resultMap.get("System Architecture");
        assertNotNull(result3);
        assertThat("systemArchitecture").isEqualTo(result3.getName());
        log.info("name: {} - description: {}",result3.getName(), result3.getDescription());
        System.out.println("----------------");
    }

    @Test
    @DisplayName("search method with special phase insist comma: special phase is at the end")
    @Transactional
    public void testSearchPhaseWithSpecialPhaseAtEnding() throws Exception{
        Map<String, String> query = new HashMap<>();
        query.put("list_descriptions", "Generic review,System Architecture,Product Management - Planning, Creation and Delivery");
        Specification<Phase> result = PhaseSpecification.search(query);

        List<Phase> phaseList = phaseRepository.findAll(result);
        Map<String, Phase> resultMap = new HashMap<>();
        phaseList.forEach(phase -> resultMap.put(phase.getDescription(), phase));
        assertThat(phaseList).hasSize(3);

        System.out.println("----------------");
        log.info("input: Generic review,System Architecture,Product Management - Planning, Creation and Delivery");

        Phase result1 = resultMap.get("Product Management - Planning, Creation and Delivery");
        assertNotNull(result1);
        assertThat("prjPlanCreationDeli").isEqualTo(result1.getName());
        log.info("name: {} - description: {}",result1.getName(), result1.getDescription());

        Phase result2 = resultMap.get("Generic review");
        assertNotNull(result2);
        assertThat("genericReview").isEqualTo(result2.getName());
        log.info("name: {} - description: {}",result2.getName(), result2.getDescription());

        Phase result3 = resultMap.get("System Architecture");
        assertNotNull(result3);
        assertThat("systemArchitecture").isEqualTo(result3.getName());
        log.info("name: {} - description: {}",result3.getName(), result3.getDescription());

        System.out.println("----------------");
    }

    @Test
    @DisplayName("search method with special phase insist comma: special phase is at the middle")
    @Transactional
    public void testSearchPhaseWithSpecialPhaseAtMiddle() throws Exception{
        Map<String, String> query = new HashMap<>();
        query.put("list_descriptions", "Generic review,System Architecture,Product Management - Planning, Creation and Delivery,CI Dashboard,Software Construction");
        Specification<Phase> result = PhaseSpecification.search(query);

        List<Phase> phaseList = phaseRepository.findAll(result);
        Map<String, Phase> resultMap = new HashMap<>();
        phaseList.forEach(phase -> resultMap.put(phase.getDescription(), phase));
        assertThat(phaseList).hasSize(5);
        
        System.out.println("----------------");
        log.info("input: Generic review,System Architecture,Product Management - Planning, Creation and Delivery,CI Dashboard,Software Construction");

        Phase result1 = resultMap.get("Product Management - Planning, Creation and Delivery");
        assertNotNull(result1);
        assertThat("prjPlanCreationDeli").isEqualTo(result1.getName());
        log.info("name: {} - description: {}",result1.getName(), result1.getDescription());

        Phase result2 = resultMap.get("Generic review");
        assertNotNull(result2);
        assertThat("genericReview").isEqualTo(result2.getName());
        log.info("name: {} - description: {}",result2.getName(), result2.getDescription());

        Phase result3 = resultMap.get("System Architecture");
        assertNotNull(result3);
        assertThat("systemArchitecture").isEqualTo(result3.getName());
        log.info("name: {} - description: {}",result3.getName(), result3.getDescription());

        Phase result4 = resultMap.get("CI Dashboard");
        assertNotNull(result4);
        assertThat("ciDashboard").isEqualTo(result4.getName());
        log.info("name: {} - description: {}",result4.getName(), result4.getDescription());

        Phase result5 = resultMap.get("Software Construction");
        assertNotNull(result5);
        assertThat("softwareConstruction").isEqualTo(result5.getName());
        log.info("name: {} - description: {}",result5.getName(), result5.getDescription());

        System.out.println("----------------");
    }
}
