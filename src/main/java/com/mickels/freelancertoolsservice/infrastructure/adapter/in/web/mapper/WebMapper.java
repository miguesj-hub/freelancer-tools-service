package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web.mapper;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/** Converts between generated API DTOs and domain models/values. */
public final class WebMapper {

    private WebMapper() {
    }

    // ---- Client ----

    public static com.mickels.freelancertoolsservice.api.model.Client toDto(
            com.mickels.freelancertoolsservice.domain.model.Client c) {
        return new com.mickels.freelancertoolsservice.api.model.Client()
                .id(idToString(c.getId()))
                .name(c.getName())
                .contactEmail(c.getContactEmail())
                .notes(c.getNotes())
                .createdAt(toOffset(c.getCreatedAt()));
    }

    // ---- Project ----

    public static com.mickels.freelancertoolsservice.api.model.Project toDto(
            com.mickels.freelancertoolsservice.domain.model.Project p) {
        return new com.mickels.freelancertoolsservice.api.model.Project()
                .id(idToString(p.getId()))
                .clientId(idToString(p.getClientId()))
                .name(p.getName())
                .description(p.getDescription())
                .createdAt(toOffset(p.getCreatedAt()));
    }

    // ---- Task ----

    public static com.mickels.freelancertoolsservice.api.model.Task toDto(
            com.mickels.freelancertoolsservice.domain.model.Task t) {
        return new com.mickels.freelancertoolsservice.api.model.Task()
                .id(idToString(t.getId()))
                .projectId(idToString(t.getProjectId()))
                .title(t.getTitle())
                .description(t.getDescription())
                .status(com.mickels.freelancertoolsservice.api.model.TaskStatus.valueOf(t.getStatus().name()))
                .createdAt(toOffset(t.getCreatedAt()));
    }

    // ---- TimeEntry ----

    public static com.mickels.freelancertoolsservice.api.model.TimeEntry toDto(
            com.mickels.freelancertoolsservice.domain.model.TimeEntry e) {
        return new com.mickels.freelancertoolsservice.api.model.TimeEntry()
                .id(idToString(e.getId()))
                .taskId(idToString(e.getTaskId()))
                .projectId(idToString(e.getProjectId()))
                .clientId(idToString(e.getClientId()))
                .minutes(e.getMinutes())
                .workDate(e.getWorkDate())
                .type(com.mickels.freelancertoolsservice.api.model.TimeEntryType.valueOf(e.getType().name()))
                .description(e.getDescription())
                .createdAt(toOffset(e.getCreatedAt()));
    }

    // ---- HoursReport ----

    public static com.mickels.freelancertoolsservice.api.model.HoursReport toDto(
            com.mickels.freelancertoolsservice.domain.model.HoursReport r) {
        return new com.mickels.freelancertoolsservice.api.model.HoursReport()
                .clientId(idToString(r.getClientId()))
                .projectId(idToString(r.getProjectId()))
                .taskId(idToString(r.getTaskId()))
                .billableMinutes((int) r.getBillableMinutes())
                .administrativeMinutes((int) r.getAdministrativeMinutes())
                .totalMinutes((int) r.getTotalMinutes());
    }

    // ---- Enums ----

    public static TaskStatus toDomain(com.mickels.freelancertoolsservice.api.model.TaskStatus status) {
        return TaskStatus.valueOf(status.name());
    }

    public static TimeEntryType toDomain(com.mickels.freelancertoolsservice.api.model.TimeEntryType type) {
        return type == null ? null : TimeEntryType.valueOf(type.name());
    }

    // ---- Primitives ----

    public static UUID parseId(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid identifier: " + value);
        }
    }

    private static String idToString(UUID id) {
        return id == null ? null : id.toString();
    }

    private static OffsetDateTime toOffset(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }
}
