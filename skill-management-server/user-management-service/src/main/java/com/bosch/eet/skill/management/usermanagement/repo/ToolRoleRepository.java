package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bosch.eet.skill.management.usermanagement.entity.ToolRole;

public interface ToolRoleRepository extends JpaRepository<ToolRole, String>{

	@Query(value = "SELECT * FROM tool_role tr LEFT JOIN (SELECT * FROM user_tool_role WHERE user_id = :userId) utr "
			+ "ON utr.tool_role_id = tr.id WHERE tr.status = :status", nativeQuery = true)
	List<ToolRole> findAllByUserId(@Param("userId") final String userId, @Param("status") final String status);
}
