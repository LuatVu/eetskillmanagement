package com.bosch.eet.skill.management.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bosch.eet.skill.management.entity.FileStorage;

public interface FileStorageRepository extends JpaRepository<FileStorage, String> {

    Optional<FileStorage> findById(String id);

    Optional<FileStorage> findByToken(String token);

    FileStorage findByNameStartingWith(String layoutPrefix);
    
    Optional<FileStorage> findByName(String name);
    
    List<FileStorage> findByNameEndingWith(String nameSuffix);
}
