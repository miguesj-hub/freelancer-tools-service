package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.domain.model.Project;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectPersistenceAdapter implements ProjectRepository {

    private final ProjectJpaRepository jpa;

    public ProjectPersistenceAdapter(ProjectJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Project save(Project project) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(project)));
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<Project> findByClientId(UUID clientId) {
        return jpa.findByClientId(clientId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByClientId(UUID clientId) {
        return jpa.existsByClientId(clientId);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
