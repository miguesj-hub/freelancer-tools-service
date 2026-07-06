package com.mickels.freelancertoolsservice.application.port.out;

import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Outbound port for time-entry persistence and hours aggregation. */
public interface TimeEntryRepository {

    TimeEntry save(TimeEntry timeEntry);

    Optional<TimeEntry> findById(UUID id);

    List<TimeEntry> findByTaskId(UUID taskId);

    /** Used to protect historical data: a task with time entries cannot be deleted (FR-013). */
    boolean existsByTaskId(UUID taskId);

    /**
     * Sum of minutes for the given type within an optional scope. Any of the scope
     * arguments may be null (meaning "no filter on that dimension") (FR-012).
     */
    long sumMinutes(TimeEntryType type, UUID clientId, UUID projectId, UUID taskId);
}
