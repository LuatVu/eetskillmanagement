package com.bosch.eet.skill.management.entity;

import com.bosch.eet.skill.management.enums.Location;
import com.bosch.eet.skill.management.enums.Status;
import com.bosch.eet.skill.management.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`supply_demand`")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SupplyDemand extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "sub_id")
    Long subId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_cluster_id", nullable = false)
    SkillGroup skillGroup;

    @Column(name = "skill")
    String skill;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    Level level;

    @Column(name = "assignee")
    String assignee;

    @Column(name = "assign_user_name")
    String assignUserName;

    @Column(name = "assign_user_ntid")
    String assignNtId;

    @Column(name = "candidate_name")
    String candidateName;

    @Column(name = "allow_external")
    Boolean allowExternal;

    @Column(name = "supply_type")
    String supplyType;

    @Column(name = "location")
    @Enumerated(EnumType.STRING)
    Location location;

    @Column(name = "expected_date")
    LocalDate expectedDate;

    @Column(name = "forecast_date")
    LocalDate forecastDate;

    @Column(name = "filled_date")
    LocalDate filledDate;
    
    @Builder.Default
    @Column(name = "status")
    Status status = Status.DRAFT;

    @Column(name = "note")
    String note;
   
    @OneToMany(mappedBy = "supplyDemand", cascade = CascadeType.ALL)
    @Singular
    List<HistorySupplyDemand> historySupplyDemands;

}
