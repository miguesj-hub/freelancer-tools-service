package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TimeEntryPersistenceAdapter.class)
class HoursReportQueryTest {

    @Autowired
    TimeEntryPersistenceAdapter adapter;

    @Test
    @DisplayName("Given entries of both types, when summing by type, then totals are grouped and scopable (FR-012)")
    void sumsByTypeAndScope() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID otherClient = UUID.randomUUID();

        adapter.save(TimeEntry.create(taskId, projectId, clientId, 90, LocalDate.now(), TimeEntryType.BILLABLE, null));
        adapter.save(TimeEntry.create(taskId, projectId, clientId, 30, LocalDate.now(), TimeEntryType.ADMINISTRATIVE, null));
        adapter.save(TimeEntry.create(UUID.randomUUID(), UUID.randomUUID(), otherClient, 60, LocalDate.now(), TimeEntryType.BILLABLE, null));

        // Unscoped: sums across all clients
        assertThat(adapter.sumMinutes(TimeEntryType.BILLABLE, null, null, null)).isEqualTo(150);

        // Scoped to the client
        assertThat(adapter.sumMinutes(TimeEntryType.BILLABLE, clientId, null, null)).isEqualTo(90);
        assertThat(adapter.sumMinutes(TimeEntryType.ADMINISTRATIVE, clientId, null, null)).isEqualTo(30);

        // Scoped to the task
        assertThat(adapter.sumMinutes(TimeEntryType.BILLABLE, null, null, taskId)).isEqualTo(90);

        // Empty scope yields zero (coalesce)
        assertThat(adapter.sumMinutes(TimeEntryType.BILLABLE, UUID.randomUUID(), null, null)).isZero();
    }
}
