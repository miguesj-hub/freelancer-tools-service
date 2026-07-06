package com.mickels.freelancertoolsservice.domain.model;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Enterprise entity: a recorded duration of work against exactly one task. The
 * task/project/client association is set at creation and never changes (FR-009).
 * Duration must be positive (FR-014); type defaults to BILLABLE (FR-011).
 */
@Getter
public class TimeEntry {

    private final UUID id;
    private final UUID taskId;
    private final UUID projectId;
    private final UUID clientId;
    private final int minutes;
    private final LocalDate workDate;
    private final TimeEntryType type;
    private final String description;
    private final Instant createdAt;

    public TimeEntry(UUID id, UUID taskId, UUID projectId, UUID clientId, int minutes,
                     LocalDate workDate, TimeEntryType type, String description, Instant createdAt) {
        this.id = id;
        this.taskId = requireRef(taskId, "task");
        this.projectId = requireRef(projectId, "project");
        this.clientId = requireRef(clientId, "client");
        this.minutes = requirePositive(minutes);
        this.workDate = requireWorkDate(workDate);
        this.type = type == null ? TimeEntryType.BILLABLE : type;
        this.description = description;
        this.createdAt = createdAt;
    }

    /** New time entry; type defaults to BILLABLE when {@code type} is null (FR-011). */
    public static TimeEntry create(UUID taskId, UUID projectId, UUID clientId, int minutes,
                                   LocalDate workDate, TimeEntryType type, String description) {
        return new TimeEntry(null, taskId, projectId, clientId, minutes, workDate, type, description, null);
    }

    /** A copy re-classified to a new type (FR-011); associations are preserved (FR-009). */
    public TimeEntry withType(TimeEntryType newType) {
        if (newType == null) {
            throw new ValidationException("Time entry type must not be null");
        }
        return new TimeEntry(this.id, this.taskId, this.projectId, this.clientId, this.minutes,
                this.workDate, newType, this.description, this.createdAt);
    }

    private static UUID requireRef(UUID ref, String name) {
        if (ref == null) {
            throw new ValidationException("Time entry must reference a " + name);
        }
        return ref;
    }

    private static int requirePositive(int minutes) {
        if (minutes <= 0) {
            throw new ValidationException("Time entry minutes must be greater than 0");
        }
        return minutes;
    }

    private static LocalDate requireWorkDate(LocalDate workDate) {
        if (workDate == null) {
            throw new ValidationException("Time entry workDate must not be null");
        }
        return workDate;
    }
}
