package com.bosch.eet.skill.management.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.entity.RequestEvaluationDetail;
import com.bosch.eet.skill.management.entity.Skill;

public interface RequestEvaluationDetailRepository extends JpaRepository<RequestEvaluationDetail, String>, JpaSpecificationExecutor {
        
    Optional<RequestEvaluationDetail> findByRequestEvaluationId(String requestEvaluationId);

    List<RequestEvaluationDetail> findBySkillId(String skillId);
    
    @Modifying
    @Query("update RequestEvaluationDetail rED"
    		+ " set rED.status = :status where rED.requestEvaluation.id = :requestId "
    		+ "AND rED.approver.id = :approverId "
    		+ "AND rED.status = 'APPROVAL_PENDING' ")
    void updateStatusByRequestIdAndApproverId(String status, String requestId, String approverId);
    
    @Query(" select red.currentLevel  from RequestEvaluationDetail red "
    		+ "left join RequestEvaluation re on red.requestEvaluation.id = re.id "
    		+ "where red.status = :status and re.requester.id= :personalId "
    		+ "and red.skill.id = :skillId ")
    Integer findEvaluatedLevel(String status, String skillId, String personalId);
    
    @Query(" select red.currentExp  from RequestEvaluationDetail red "
    		+ "left join RequestEvaluation re on red.requestEvaluation.id = re.id "
    		+ "where red.status = :status and re.requester.id= :personalId "
    		+ "and red.skill.id = :skillId ")
    Integer findEvaluatedExperience(String status, String skillId, String personalId);
    
    @Query(" select red.comment  from RequestEvaluationDetail red "
    		+ "left join RequestEvaluation re on red.requestEvaluation.id = re.id "
    		+ "where red.status = :status and re.requester.id= :personalId "
    		+ "and red.skill.id = :skillId ")
    String findEvaluatedComment(String status, String skillId, String personalId);
    
    @Modifying
    @Query("delete from RequestEvaluationDetail red where red.requestEvaluation.id = :id and red.status = :status")
    void deleteAllByRequestIdAndStatus(String id, String status);
    
    @Query("select distinct red.approver.id from RequestEvaluationDetail red"
    		+ " where red.approver.id not in (select distinct approver.id from RequestEvaluation) and red.status =:status")
    List<String> findIdCompetencyLead(String status);
    
    @Query("select red from RequestEvaluationDetail red where red.requestEvaluation.requester=:requester"
    		+ " and red.status='APPROVED' and red.skill=:skill and red.createdDate<:createdDate order by red.createdDate desc")
    List<RequestEvaluationDetail> findRequestEvaluatedDetailByRequesterAndSkillAndCreatedDate(Personal requester, Skill skill, Date createdDate,Pageable page);

    @Modifying
    @Query("delete from RequestEvaluationDetail red where red.skill.id = :skillId")
    void deleteBySkillId(String skillId);
}