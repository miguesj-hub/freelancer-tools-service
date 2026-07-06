package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassifyTimeEntryServiceTest {

    @Mock TimeEntryRepository timeEntryRepository;

    ClassifyTimeEntryService service;

    @BeforeEach
    void setUp() {
        service = new ClassifyTimeEntryService(timeEntryRepository);
    }

    @Test
    @DisplayName("Given a time entry, when re-classifying, then the new type is saved")
    void reclassifies() {
        UUID id = UUID.randomUUID();
        TimeEntry existing = new TimeEntry(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                30, LocalDate.now(), TimeEntryType.BILLABLE, null, null);
        when(timeEntryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(timeEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TimeEntry result = service.classify(id, TimeEntryType.ADMINISTRATIVE);
        assertThat(result.getType()).isEqualTo(TimeEntryType.ADMINISTRATIVE);
    }

    @Test
    @DisplayName("Given a missing time entry, when re-classifying, then not-found is raised")
    void rejectsMissing() {
        UUID id = UUID.randomUUID();
        when(timeEntryRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.classify(id, TimeEntryType.BILLABLE))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
