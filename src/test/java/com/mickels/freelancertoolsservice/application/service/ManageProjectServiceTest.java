package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ManageProjectUseCase.ProjectCommand;
import com.mickels.freelancertoolsservice.application.port.out.ClientRepository;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageProjectServiceTest {

    @Mock ProjectRepository projectRepository;
    @Mock ClientRepository clientRepository;
    @Mock TaskRepository taskRepository;

    ManageProjectService service;

    @BeforeEach
    void setUp() {
        service = new ManageProjectService(projectRepository, clientRepository, taskRepository);
    }

    @Test
    @DisplayName("Given a non-existent client, when creating a project, then not-found is raised")
    void createRejectsMissingClient() {
        UUID clientId = UUID.randomUUID();
        when(clientRepository.existsById(clientId)).thenReturn(false);
        assertThatThrownBy(() -> service.create(clientId, new ProjectCommand("P", null)))
                .isInstanceOf(EntityNotFoundException.class);
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given an existing client, when creating a project, then it is saved")
    void createsProject() {
        UUID clientId = UUID.randomUUID();
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(projectRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Project created = service.create(clientId, new ProjectCommand("Website", "d"));
        assertThat(created.getClientId()).isEqualTo(clientId);
    }

    @Test
    @DisplayName("Given a missing project, when getting, then not-found is raised")
    void getMissing() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given a non-existent client, when listing its projects, then not-found is raised")
    void listRejectsMissingClient() {
        UUID clientId = UUID.randomUUID();
        when(clientRepository.existsById(clientId)).thenReturn(false);
        assertThatThrownBy(() -> service.listByClient(clientId)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given an existing client, when listing, then its projects are returned")
    void listsProjects() {
        UUID clientId = UUID.randomUUID();
        when(clientRepository.existsById(clientId)).thenReturn(true);
        when(projectRepository.findByClientId(clientId))
                .thenReturn(List.of(new Project(UUID.randomUUID(), clientId, "P", null, Instant.now())));
        assertThat(service.listByClient(clientId)).hasSize(1);
    }

    @Test
    @DisplayName("Given a project with tasks, when deleting, then it is rejected (FR-013)")
    void deleteWithTasksRejected() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(true);
        when(taskRepository.existsByProjectId(id)).thenReturn(true);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(DependentRecordsException.class);
        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Given a missing project, when deleting, then not-found is raised")
    void deleteMissing() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given a project without tasks, when deleting, then it is removed")
    void deleteSucceeds() {
        UUID id = UUID.randomUUID();
        when(projectRepository.existsById(id)).thenReturn(true);
        when(taskRepository.existsByProjectId(id)).thenReturn(false);
        service.delete(id);
        verify(projectRepository).deleteById(id);
    }
}
