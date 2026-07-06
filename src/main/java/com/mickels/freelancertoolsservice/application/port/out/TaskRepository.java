package com.mickels.freelancertoolsservice.application.port.out;

import com.mickels.freelancertoolsservice.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Outbound port for task persistence. */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findByProjectId(UUID projectId);

    boolean existsById(UUID id);

    /** Used to protect historical data: a project with tasks cannot be deleted (FR-013). */
    boolean existsByProjectId(UUID projectId);

    void deleteById(UUID id);
}
