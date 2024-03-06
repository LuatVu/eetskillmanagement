package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bosch.eet.skill.management.usermanagement.entity.Permission;

public interface PermissionRepository extends CrudRepository<Permission, String> {
	Permission findByName(String namePermission);
 	void deleteByName(String namePermission);

	@Query(value = "SELECT DISTINCT pr from Permission pr join RolePermission rp ON pr.id=rp.permission.id where pr.status='ACTIVE' AND rp.role.id IN (:roleIds)")
	List<Permission> findAllByRoles(@Param("roleIds") final List<String> roleIds);

	@Query(value = "Select p from Permission p where p.id in (:permissionIds)")
	List<Permission> findPermissionsById(List<String> permissionIds);
}
