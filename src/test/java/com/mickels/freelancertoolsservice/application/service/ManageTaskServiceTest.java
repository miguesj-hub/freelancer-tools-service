package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ManageTaskUseCase.TaskCommand;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageTaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock ProjectRepository projectRepository;
    @Mock TimeEntryRepository timeEntryRepository;

    ManageTaskService service;

    @BeforeEach
    void setUp() {
        service = new ManageTaskService(taskRepository, projectRepository, timeEntryRepository);
    }

    @Test
    @DisplayName("Given a non-existent project, when creating a task, then not-found is raised")
    void createRejectsMissingProject() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(false);
        assertThatThrownBy(() -> service.create(projectId, new TaskCommand("t", null, null)))
                .isInstanceOf(EntityNotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given an existing project, when creating a task, then it is saved as TO_DO")
    void createsTask() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Task created = service.create(projectId, new TaskCommand("Design", null, null));
        assertThat(created.getStatus()).isEqualTo(TaskStatus.TO_DO);
    }

    @Test
    @DisplayName("Given notes in the command, when creating a task, then the notes are persisted (FR-016)")
    void createsTaskWithNotes() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Task created = service.create(projectId, new TaskCommand("Design", null, "blocked on assets"));
        assertThat(created.getNotes()).isEqualTo("blocked on assets");
    }

    @Test
    @DisplayName("Given a task, when updating status, then the new status is saved")
    void updatesStatus() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id))
                .thenReturn(Optional.of(new Task(id, UUID.randomUUID(), "t", null, null, TaskStatus.TO_DO, Instant.now())));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Task updated = service.updateStatus(id, TaskStatus.IN_PROGRESS);
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Given a non-existent project, when listing tasks, then not-found is raised")
    void listRejectsMissingProject() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(false);
        assertThatThrownBy(() -> service.listByProject(projectId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given an existing project, when listing tasks, then tasks are returned")
    void listsTasks() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(taskRepository.findByProjectId(projectId)).thenReturn(java.util.List.of());
        assertThat(service.listByProject(projectId)).isEmpty();
    }

    @Test
    @DisplayName("Given a task with time entries, when deleting, then it is rejected (FR-013)")
    void deleteWithEntriesRejected() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(true);
        when(timeEntryRepository.existsByTaskId(id)).thenReturn(true);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(DependentRecordsException.class);
    }

    @Test
    @DisplayName("Given a missing task, when deleting, then not-found is raised")
    void deleteMissing() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given a task without time entries, when deleting, then it is removed")
    void deleteSucceeds() {
        UUID id = UUID.randomUUID();
        when(taskRepository.existsById(id)).thenReturn(true);
        when(timeEntryRepository.existsByTaskId(id)).thenReturn(false);
        service.delete(id);
        verify(taskRepository).deleteById(id);
    }
}
