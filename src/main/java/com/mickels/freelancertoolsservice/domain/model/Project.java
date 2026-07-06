package com.mickels.freelancertoolsservice.domain.model;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Enterprise entity: a project belonging to exactly one client. Owns tasks;
 * cannot be deleted while tasks exist (FR-013).
 */
@Getter
public class Project {

    private final UUID id;
    private final UUID clientId;
    private final String name;
    private final String description;
    private final Instant createdAt;

    public Project(UUID id, UUID clientId, String name, String description, Instant createdAt) {
        this.id = id;
        this.clientId = requireClientId(clientId);
        this.name = requireName(name);
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Project create(UUID clientId, String name, String description) {
        return new Project(null, clientId, name, description, null);
    }

    private static UUID requireClientId(UUID clientId) {
        if (clientId == null) {
            throw new ValidationException("Project must reference a client");
        }
        return clientId;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Project name must not be blank");
        }
        if (name.length() > 120) {
            throw new ValidationException("Project name must be at most 120 characters");
        }
        return name;
    }
}
