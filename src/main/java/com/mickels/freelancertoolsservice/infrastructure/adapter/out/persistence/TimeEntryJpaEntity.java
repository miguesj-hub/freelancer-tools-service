package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "time_entries")
@Getter
@Setter
@NoArgsConstructor
public class TimeEntryJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID taskId;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private int minutes;

    @Column(nullable = false)
    private LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TimeEntryType type;

    @Column(length = 2000)
    private String description;
}
