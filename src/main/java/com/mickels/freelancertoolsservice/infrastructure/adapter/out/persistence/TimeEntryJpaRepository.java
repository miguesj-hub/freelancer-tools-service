package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface TimeEntryJpaRepository extends JpaRepository<TimeEntryJpaEntity, UUID> {

    List<TimeEntryJpaEntity> findByTaskId(UUID taskId);

    boolean existsByTaskId(UUID taskId);

    @Query("""
            select coalesce(sum(t.minutes), 0) from TimeEntryJpaEntity t
            where t.type = :type
              and (:clientId is null or t.clientId = :clientId)
              and (:projectId is null or t.projectId = :projectId)
              and (:taskId is null or t.taskId = :taskId)
            """)
    long sumMinutes(@Param("type") TimeEntryType type,
                    @Param("clientId") UUID clientId,
                    @Param("projectId") UUID projectId,
                    @Param("taskId") UUID taskId);
}
