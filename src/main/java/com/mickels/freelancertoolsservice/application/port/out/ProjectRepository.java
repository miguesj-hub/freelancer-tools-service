package com.mickels.freelancertoolsservice.application.port.out;

import com.mickels.freelancertoolsservice.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Outbound port for project persistence. */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findByClientId(UUID clientId);

    boolean existsById(UUID id);

    /** Used to protect historical data: a client with projects cannot be deleted (FR-013). */
    boolean existsByClientId(UUID clientId);

    void deleteById(UUID id);
}
