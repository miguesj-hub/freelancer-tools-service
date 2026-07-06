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
class TimeEntryPersistenceAdapterTest {

    @Autowired
    TimeEntryPersistenceAdapter adapter;

    @Test
    @DisplayName("Given multiple entries for a task, when queried, then all are returned (FR-010)")
    void storesMultipleEntriesPerTask() {
        UUID taskId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        adapter.save(TimeEntry.create(taskId, projectId, clientId, 30, LocalDate.now(), TimeEntryType.BILLABLE, null));
        adapter.save(TimeEntry.create(taskId, projectId, clientId, 15, LocalDate.now(), TimeEntryType.ADMINISTRATIVE, null));

        assertThat(adapter.findByTaskId(taskId)).hasSize(2);
        assertThat(adapter.existsByTaskId(taskId)).isTrue();
        assertThat(adapter.existsByTaskId(UUID.randomUUID())).isFalse();
    }

    @Test
    @DisplayName("Given a saved entry, when re-read, then all associations round-trip (FR-009)")
    void associationsRoundTrip() {
        UUID taskId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        TimeEntry saved = adapter.save(
                TimeEntry.create(taskId, projectId, clientId, 45, LocalDate.now(), TimeEntryType.BILLABLE, "d"));

        TimeEntry read = adapter.findById(saved.getId()).orElseThrow();
        assertThat(read.getTaskId()).isEqualTo(taskId);
        assertThat(read.getProjectId()).isEqualTo(projectId);
        assertThat(read.getClientId()).isEqualTo(clientId);
        assertThat(read.getMinutes()).isEqualTo(45);
    }
}
