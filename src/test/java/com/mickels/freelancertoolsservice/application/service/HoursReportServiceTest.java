package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.model.HoursReport;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HoursReportServiceTest {

    @Mock TimeEntryRepository timeEntryRepository;

    HoursReportService service;

    @BeforeEach
    void setUp() {
        service = new HoursReportService(timeEntryRepository);
    }

    @Test
    @DisplayName("Given billable and administrative entries, when reporting, then hours are grouped by type (FR-012)")
    void groupsByType() {
        UUID clientId = UUID.randomUUID();
        when(timeEntryRepository.sumMinutes(TimeEntryType.BILLABLE, clientId, null, null)).thenReturn(90L);
        when(timeEntryRepository.sumMinutes(TimeEntryType.ADMINISTRATIVE, clientId, null, null)).thenReturn(30L);

        HoursReport report = service.report(clientId, null, null);

        assertThat(report.getBillableMinutes()).isEqualTo(90);
        assertThat(report.getAdministrativeMinutes()).isEqualTo(30);
        assertThat(report.getTotalMinutes()).isEqualTo(120);
    }
}
