package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto;
import com.bosch.eet.skill.management.entity.RequestEvaluation;

public interface RequestEvaluationRepository extends JpaRepository<RequestEvaluation, String>, JpaSpecificationExecutor {
        
    List<RequestEvaluation> findByApproverId(String approverId);   
    
    Optional<RequestEvaluation> findById (String id);
    
    @Query("select new com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto(re.approver.user.displayName, re.approver.user.email,"
    		+ "count(distinct re)) from RequestEvaluation as re where re.status='APPROVAL_PENDING' group by re.approver")
    public List<RequestEvaluationPendingDto> findRequestPending();
    
    @Query("select new com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto"
    		+ "(red.approver.user.displayName, red.approver.user.email, count(distinct red.requestEvaluation))"
    		+ " from RequestEvaluationDetail red where red.status='APPROVAL_PENDING' group by red.approver")
    public List<RequestEvaluationPendingDto> findRequestPendingNew();
    
    @Query("select new com.bosch.eet.skill.management.dto.RequestEvaluationPendingDto"
    		+ "(red.approver.user.displayName, red.approver.user.email, count(distinct red.requestEvaluation))"
    		+ " from RequestEvaluationDetail red where red.status=:status and red.approver.id=:personalId")
    public RequestEvaluationPendingDto findRequestPendingByPersonalIdAndStatus(String personalId,String status);
    
	@Query("select count(re) from RequestEvaluation re where re.approver.id=:personalId and re.status=:status and"
			+ " re not in (select distinct red.requestEvaluation from RequestEvaluationDetail red where "
			+ " red.approver.id=:personalId and red.status=:status) ")
	public Long countRequestPendingByPersonalIdAndStatus(String personalId, String status);
    
    @Query("select re from RequestEvaluation re join User u on re.approver.id = u.id where u.name =:ntid and re.status = :status")
    public List<RequestEvaluation> findAllByApproverIdAndStatus(String ntid ,String status);
    
    @Query("select re from RequestEvaluation re join User u on re.approver.id = u.id where  u.name =:ntid and re.status != :status")
    public List<RequestEvaluation> findAllRequestEvaluated(String ntid ,String status);
}