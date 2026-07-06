package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.LogTimeUseCase.LogTimeCommand;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Project;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import com.mickels.freelancertoolsservice.domain.vo.TimeEntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogTimeServiceTest {

    @Mock TimeEntryRepository timeEntryRepository;
    @Mock TaskRepository taskRepository;
    @Mock ProjectRepository projectRepository;

    LogTimeService service;

    UUID taskId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    UUID clientId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new LogTimeService(timeEntryRepository, taskRepository, projectRepository);
    }

    @Test
    @DisplayName("Given an existing task, when logging time, then the entry is tied to task, project and client (FR-009)")
    void logsTimeWithFullAssociation() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(new Task(taskId, projectId, "t", null, TaskStatus.TO_DO, Instant.now())));
        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(new Project(projectId, clientId, "p", null, Instant.now())));
        when(timeEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TimeEntry entry = service.log(taskId, new LogTimeCommand(90, LocalDate.now(), null, null));

        assertThat(entry.getTaskId()).isEqualTo(taskId);
        assertThat(entry.getProjectId()).isEqualTo(projectId);
        assertThat(entry.getClientId()).isEqualTo(clientId);
        assertThat(entry.getType()).isEqualTo(TimeEntryType.BILLABLE);
    }

    @Test
    @DisplayName("Given a non-existent task, when logging time, then not-found is raised")
    void rejectsMissingTask() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.log(taskId, new LogTimeCommand(30, LocalDate.now(), null, null)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given a task whose project is missing, when logging time, then not-found is raised")
    void rejectsMissingProject() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(new Task(taskId, projectId, "t", null, TaskStatus.TO_DO, Instant.now())));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.log(taskId, new LogTimeCommand(30, LocalDate.now(), null, null)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given an existing task, when logging multiple entries, then all are retrievable (FR-010)")
    void listsMultipleEntries() {
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(timeEntryRepository.findByTaskId(taskId)).thenReturn(List.of(
                TimeEntry.create(taskId, projectId, clientId, 30, LocalDate.now(), TimeEntryType.BILLABLE, null),
                TimeEntry.create(taskId, projectId, clientId, 15, LocalDate.now(), TimeEntryType.ADMINISTRATIVE, null)));
        assertThat(service.listByTask(taskId)).hasSize(2);
    }

    @Test
    @DisplayName("Given a non-existent task, when listing entries, then not-found is raised")
    void listRejectsMissingTask() {
        when(taskRepository.existsById(taskId)).thenReturn(false);
        assertThatThrownBy(() -> service.listByTask(taskId)).isInstanceOf(EntityNotFoundException.class);
    }
}
