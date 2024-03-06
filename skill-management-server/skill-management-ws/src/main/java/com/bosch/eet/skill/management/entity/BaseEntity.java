package com.bosch.eet.skill.management.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.GenericGenerator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    String id;

    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "created_by_ntid")
    private String createdByNtid;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by_name")
    private String updatedByName;

    @Column(name = "updated_by_ntid")
    private String updatedByNtid;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    void preInsert() {
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
