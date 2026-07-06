package com.mickels.freelancertoolsservice.domain.model;

import lombok.Getter;

import java.util.UUID;

/**
 * Derived (non-persisted) reporting model: total minutes grouped by classification,
 * within an optional scope (client/project/task) (FR-012).
 */
@Getter
public class HoursReport {

    private final UUID clientId;
    private final UUID projectId;
    private final UUID taskId;
    private final long billableMinutes;
    private final long administrativeMinutes;

    public HoursReport(UUID clientId, UUID projectId, UUID taskId,
                       long billableMinutes, long administrativeMinutes) {
        this.clientId = clientId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.billableMinutes = billableMinutes;
        this.administrativeMinutes = administrativeMinutes;
    }

    public long getTotalMinutes() {
        return billableMinutes + administrativeMinutes;
    }
}
