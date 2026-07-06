package com.mickels.freelancertoolsservice.domain.model;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Enterprise entity: a unit of work belonging to exactly one project. Defaults to
 * {@link TaskStatus#TO_DO} (FR-006); transitions freely among the three states (FR-007).
 * Cannot be deleted while time entries exist (FR-013).
 */
@Getter
public class Task {

    private final UUID id;
    private final UUID projectId;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final Instant createdAt;

    public Task(UUID id, UUID projectId, String title, String description,
                TaskStatus status, Instant createdAt) {
        this.id = id;
        this.projectId = requireProjectId(projectId);
        this.title = requireTitle(title);
        this.description = description;
        this.status = status == null ? TaskStatus.TO_DO : status;
        this.createdAt = createdAt;
    }

    /** New task under a project; status defaults to TO_DO (FR-006). */
    public static Task create(UUID projectId, String title, String description) {
        return new Task(null, projectId, title, description, TaskStatus.TO_DO, null);
    }

    /** A copy with a new status (FR-007). Any of the three states is allowed. */
    public Task withStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new ValidationException("Task status must not be null");
        }
        return new Task(this.id, this.projectId, this.title, this.description, newStatus, this.createdAt);
    }

    private static UUID requireProjectId(UUID projectId) {
        if (projectId == null) {
            throw new ValidationException("Task must reference a project");
        }
        return projectId;
    }

    private static String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Task title must not be blank");
        }
        if (title.length() > 200) {
            throw new ValidationException("Task title must be at most 200 characters");
        }
        return title;
    }
}
