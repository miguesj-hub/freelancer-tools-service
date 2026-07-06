package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TimeEntryPersistenceAdapter implements TimeEntryRepository {

    private final TimeEntryJpaRepository jpa;

    public TimeEntryPersistenceAdapter(TimeEntryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public TimeEntry save(TimeEntry timeEntry) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(timeEntry)));
    }

    @Override
    public Optional<TimeEntry> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<TimeEntry> findByTaskId(UUID taskId) {
        return jpa.findByTaskId(taskId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByTaskId(UUID taskId) {
        return jpa.existsByTaskId(taskId);
    }

    @Override
    public long sumMinutes(TimeEntryType type, UUID clientId, UUID projectId, UUID taskId) {
        return jpa.sumMinutes(type, clientId, projectId, taskId);
    }
}
