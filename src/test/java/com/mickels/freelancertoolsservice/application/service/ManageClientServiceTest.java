package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ManageClientUseCase.ClientCommand;
import com.mickels.freelancertoolsservice.application.port.out.ClientRepository;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Client;
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
class ManageClientServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock ProjectRepository projectRepository;

    ManageClientService service;

    @BeforeEach
    void setUp() {
        service = new ManageClientService(clientRepository, projectRepository);
    }

    @Test
    @DisplayName("Given valid details, when creating, then the client is saved")
    void createsClient() {
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Client created = service.create(new ClientCommand("Acme", null, null));
        assertThat(created.getName()).isEqualTo("Acme");
        verify(clientRepository).save(any());
    }

    @Test
    @DisplayName("Given a missing client, when getting, then not-found is raised")
    void getMissing() {
        UUID id = UUID.randomUUID();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Given an existing client, when updating, then details are persisted")
    void updatesClient() {
        UUID id = UUID.randomUUID();
        when(clientRepository.findById(id)).thenReturn(Optional.of(new Client(id, "Old", null, null, Instant.now())));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Client updated = service.update(id, new ClientCommand("New", "e@x.io", "n"));
        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Given clients exist, when listing, then all are returned")
    void listsClients() {
        when(clientRepository.findAll()).thenReturn(List.of(new Client(UUID.randomUUID(), "A", null, null, Instant.now())));
        assertThat(service.list()).hasSize(1);
    }

    @Test
    @DisplayName("Given a missing client, when deleting, then not-found is raised")
    void deleteMissing() {
        UUID id = UUID.randomUUID();
        when(clientRepository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(EntityNotFoundException.class);
        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Given a client with projects, when deleting, then it is rejected (FR-013)")
    void deleteWithProjectsRejected() {
        UUID id = UUID.randomUUID();
        when(clientRepository.existsById(id)).thenReturn(true);
        when(projectRepository.existsByClientId(id)).thenReturn(true);
        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(DependentRecordsException.class);
        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Given a client without projects, when deleting, then it is removed")
    void deleteSucceeds() {
        UUID id = UUID.randomUUID();
        when(clientRepository.existsById(id)).thenReturn(true);
        when(projectRepository.existsByClientId(id)).thenReturn(false);
        service.delete(id);
        verify(clientRepository).deleteById(id);
    }
}
