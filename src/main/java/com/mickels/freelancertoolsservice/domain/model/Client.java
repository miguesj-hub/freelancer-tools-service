package com.mickels.freelancertoolsservice.domain.model;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Enterprise entity: a client the freelancer works for. Pure Java (no framework
 * dependency). Owns projects; cannot be deleted while projects exist (FR-013).
 */
@Getter
public class Client {

    private final UUID id;
    private final String name;
    private final String contactEmail;
    private final String notes;
    private final Instant createdAt;

    public Client(UUID id, String name, String contactEmail, String notes, Instant createdAt) {
        this.id = id;
        this.name = requireName(name);
        this.contactEmail = contactEmail;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    /** New client with no persisted identity yet (id assigned on save). */
    public static Client create(String name, String contactEmail, String notes) {
        return new Client(null, name, contactEmail, notes, null);
    }

    /** A copy with updated mutable details, preserving identity and creation time. */
    public Client withDetails(String name, String contactEmail, String notes) {
        return new Client(this.id, name, contactEmail, notes, this.createdAt);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Client name must not be blank");
        }
        if (name.length() > 120) {
            throw new ValidationException("Client name must be at most 120 characters");
        }
        return name;
    }
}
