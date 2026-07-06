package com.mickels.freelancertoolsservice.domain;

import com.mickels.freelancertoolsservice.domain.model.HoursReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HoursReportTest {

    @Test
    @DisplayName("Given billable and administrative minutes, when reporting, then total is their sum")
    void totalsMinutes() {
        UUID clientId = UUID.randomUUID();
        HoursReport report = new HoursReport(clientId, null, null, 90, 30);

        assertThat(report.getClientId()).isEqualTo(clientId);
        assertThat(report.getProjectId()).isNull();
        assertThat(report.getTaskId()).isNull();
        assertThat(report.getBillableMinutes()).isEqualTo(90);
        assertThat(report.getAdministrativeMinutes()).isEqualTo(30);
        assertThat(report.getTotalMinutes()).isEqualTo(120);
    }
}
