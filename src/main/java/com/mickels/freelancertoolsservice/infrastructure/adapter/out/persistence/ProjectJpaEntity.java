package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class ProjectJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 2000)
    private String description;
}
