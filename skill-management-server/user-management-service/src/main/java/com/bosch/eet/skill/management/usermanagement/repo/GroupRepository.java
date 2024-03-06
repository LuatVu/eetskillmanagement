package com.bosch.eet.skill.management.usermanagement.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bosch.eet.skill.management.usermanagement.entity.Group;

public interface GroupRepository extends PagingAndSortingRepository<Group, String> {
    List<Group> findAllByStatus(String toString);

    Optional<Group> findByIdAndStatus(String id, String status);

    List<Group> findByNameLike(String s);

    Optional<Group> findByNameAndStatus(String groupName, String active);

    Optional<Group> findByName(String groupName);

    boolean existsGroupByName(String groupName);

    List<Group> findByNameStartingWith(String prefix);
}