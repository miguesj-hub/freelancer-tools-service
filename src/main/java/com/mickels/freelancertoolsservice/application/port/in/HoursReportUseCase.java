package com.mickels.freelancertoolsservice.application.port.in;

import com.mickels.freelancertoolsservice.domain.model.HoursReport;

import java.util.UUID;

/** Inbound port: produce an hours report grouped by classification (FR-012). */
public interface HoursReportUseCase {

    /** Any scope argument may be null (no filter on that dimension). */
    HoursReport report(UUID clientId, UUID projectId, UUID taskId);
}
