package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.Personal;
import com.bosch.eet.skill.management.usermanagement.entity.User;

public interface PersonalRepository extends JpaRepository<Personal, String>, JpaSpecificationExecutor {

    @Override
    Optional<Personal> findById(String s);

    List<Personal> findByIdIn(List<String> ids);

    Optional<Personal> findByPersonalCodeIgnoreCase(String personalCode);

    Integer countAllByLevelId(String level);

    @Query("select count(distinct u)" +
            " from Personal p join User u on p.user.id = u.id" +
            " join Team t on p.team.id = t.id" +
            " where :team is null or t.name = :team" +
            " and u.displayName not like 'MS/EET%'" +
            " and u.displayName not like 'System%'")
    Integer countAssociates(String team);

    @Query("select count(distinct u)" +
            " from Personal p join User u on p.user.id = u.id" +
            " join Team t on p.team.id = t.id" +
            " where :team is null or t.name = :team" +
            " and u.displayName like 'FIXED-TERM%'")
    Integer countFixedTerms(String team);
    
//    @Query("select p.manager from Personal as p where p.id=:idPersonal")
//    Optional<User> findLineManager(String idPersonal);

    @Query("select t.lineManager from Personal p" +
            " join Team t on t.id = p.team.id" +
            " where p.id = :idPersonal")
    Optional<User> findLineManager(String idPersonal);
    
    @Query("select p from Personal p"
    		+ " where p not in (select r from RequestEvaluation r where r.requester.id = p.id)"
    		+ " and  p.manager.id = :idManager and p.id != :idManager")
    List<Personal> findAssociatesNotEvaluateYet(String idManager);

    @Query("select p.id from Personal p")
    List<String> findAllPersonalId();

}
