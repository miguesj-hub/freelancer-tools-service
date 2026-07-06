package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ClassifyTimeEntryUseCase;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;

import java.util.UUID;

/** Application service to re-classify a time entry (FR-011). Framework-free. */
public class ClassifyTimeEntryService implements ClassifyTimeEntryUseCase {

    private final TimeEntryRepository timeEntryRepository;

    public ClassifyTimeEntryService(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @Override
    public TimeEntry classify(UUID timeEntryId, TimeEntryType type) {
        TimeEntry existing = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new EntityNotFoundException("TimeEntry", timeEntryId));
        return timeEntryRepository.save(existing.withType(type));
    }
}
