package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {

    List<TaskJpaEntity> findByProjectId(UUID projectId);

    boolean existsByProjectId(UUID projectId);
}
