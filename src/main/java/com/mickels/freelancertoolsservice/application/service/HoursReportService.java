package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.HoursReportUseCase;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.model.HoursReport;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;

import java.util.UUID;

/** Application service producing an hours report grouped by classification (FR-012). */
public class HoursReportService implements HoursReportUseCase {

    private final TimeEntryRepository timeEntryRepository;

    public HoursReportService(TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @Override
    public HoursReport report(UUID clientId, UUID projectId, UUID taskId) {
        long billable = timeEntryRepository.sumMinutes(TimeEntryType.BILLABLE, clientId, projectId, taskId);
        long administrative = timeEntryRepository.sumMinutes(TimeEntryType.ADMINISTRATIVE, clientId, projectId, taskId);
        return new HoursReport(clientId, projectId, taskId, billable, administrative);
    }
}
