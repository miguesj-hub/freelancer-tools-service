package com.mickels.freelancertoolsservice.domain;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeEntryTest {

    private final UUID task = UUID.randomUUID();
    private final UUID project = UUID.randomUUID();
    private final UUID client = UUID.randomUUID();

    @Test
    @DisplayName("Given no explicit type, when creating a time entry, then it defaults to BILLABLE")
    void defaultsToBillable() {
        TimeEntry entry = TimeEntry.create(task, project, client, 90, LocalDate.now(), null, null);
        assertThat(entry.getType()).isEqualTo(TimeEntryType.BILLABLE);
        assertThat(entry.getMinutes()).isEqualTo(90);
    }

    @Test
    @DisplayName("Given zero minutes, when creating a time entry, then validation fails")
    void rejectsZeroMinutes() {
        assertThatThrownBy(() -> TimeEntry.create(task, project, client, 0, LocalDate.now(), null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given negative minutes, when creating a time entry, then validation fails")
    void rejectsNegativeMinutes() {
        assertThatThrownBy(() -> TimeEntry.create(task, project, client, -5, LocalDate.now(), null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given no work date, when creating a time entry, then validation fails")
    void rejectsNullWorkDate() {
        assertThatThrownBy(() -> TimeEntry.create(task, project, client, 30, null, null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given missing task/project/client, when creating a time entry, then validation fails")
    void rejectsMissingReferences() {
        assertThatThrownBy(() -> TimeEntry.create(null, project, client, 30, LocalDate.now(), null, null))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> TimeEntry.create(task, null, client, 30, LocalDate.now(), null, null))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> TimeEntry.create(task, project, null, 30, LocalDate.now(), null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given a time entry, when re-classified, then type changes and associations are preserved")
    void reclassifyPreservesAssociations() {
        TimeEntry entry = TimeEntry.create(task, project, client, 30, LocalDate.now(), TimeEntryType.BILLABLE, "d");
        TimeEntry reclassified = entry.withType(TimeEntryType.ADMINISTRATIVE);

        assertThat(reclassified.getType()).isEqualTo(TimeEntryType.ADMINISTRATIVE);
        assertThat(reclassified.getTaskId()).isEqualTo(task);
        assertThat(reclassified.getProjectId()).isEqualTo(project);
        assertThat(reclassified.getClientId()).isEqualTo(client);
        assertThat(reclassified.getMinutes()).isEqualTo(30);
    }

    @Test
    @DisplayName("Given a time entry, when re-classified to null, then validation fails")
    void rejectsNullReclassification() {
        TimeEntry entry = TimeEntry.create(task, project, client, 30, LocalDate.now(), null, null);
        assertThatThrownBy(() -> entry.withType(null)).isInstanceOf(ValidationException.class);
    }
}
