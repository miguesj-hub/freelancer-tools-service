package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskPersistenceAdapter implements TaskRepository {

    private final TaskJpaRepository jpa;

    public TaskPersistenceAdapter(TaskJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Task save(Task task) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(task)));
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return jpa.findByProjectId(projectId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByProjectId(UUID projectId) {
        return jpa.existsByProjectId(projectId);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
