package com.mickels.freelancertoolsservice.application.port.in;

import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;

import java.util.UUID;

/** Inbound port: re-classify a time entry as billable or administrative (FR-011). */
public interface ClassifyTimeEntryUseCase {

    TimeEntry classify(UUID timeEntryId, TimeEntryType type);
}
