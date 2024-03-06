package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bosch.eet.skill.management.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor {

    List<Project> findAllByGbUnitId(String gbUnitId);

    @Query("select count(distinct p) from Project p" +
            " join GbUnit gbu on p.gbUnit.id = gbu.id" +
            " where :gbUnit is null or gbu.name = :gbUnit" +
            " and p.status = :status")
    Integer countDistinctByStatus(String gbUnit, String status);

    @Query("select count(distinct p) from Project p" +
            " join GbUnit gbu on p.gbUnit.id = gbu.id" +
            " where :gbUnit is null or gbu.name = :gbUnit")
    Integer countProjects(String gbUnit);

    Optional<Project> findByName(String name);

    @Query("select distinct p.status from Project p where p.status is not null")
    List<String> findProjectStatusList();

    @Query("select p from Project p where p.customerGb <> ''")
    List<Project> findAllHavingCustomerGb();
    
    List<Project> findByNameIgnoreCase(String name);

    @Query("SELECT p.id, p.name FROM Project p WHERE p.projectType.name = ?1")
    List<Object[]> findAllProjectIdName(String type);

    @Modifying
    @Query("UPDATE Project p set p.projectScope=null where p.projectScope.id=:projectScopeId")
    void deleteProjectScopeFromProject(String projectScopeId);
}
