package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class ClientJpaEntity extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String contactEmail;

    @Column(length = 2000)
    private String notes;
}
