package com.bosch.eet.skill.management.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.bosch.eet.skill.management.enums.Status;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`history_supply_demand`")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class HistorySupplyDemand implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    String id;

    @ManyToOne()
    @JoinColumn(name = "item_id", nullable = false)
    SupplyDemand supplyDemand;

    @Column(name = "old_status")
    Status oldStatus;

    @Column(name = "new_status")
    Status newStatus;

    @Column(name = "note")
    String note;

    @Column(name = "updated_by_name")
    private String updatedByName;

    @Column(name = "updated_by_ntid")
    private String updatedByNtid;

    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    void preInsert() {
       updatedDate = new Date();
    }

}
