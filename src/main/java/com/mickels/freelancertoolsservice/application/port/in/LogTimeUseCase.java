package com.mickels.freelancertoolsservice.application.port.in;

import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Inbound port: log time against a task and list a task's entries (FR-008..FR-011). */
public interface LogTimeUseCase {

    TimeEntry log(UUID taskId, LogTimeCommand command);

    List<TimeEntry> listByTask(UUID taskId);

    /** {@code type} may be null; the domain then defaults it to BILLABLE (FR-011). */
    record LogTimeCommand(int minutes, LocalDate workDate, TimeEntryType type, String description) {}
}
