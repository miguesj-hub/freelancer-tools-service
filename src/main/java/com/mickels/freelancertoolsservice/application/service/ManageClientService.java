package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ManageClientUseCase;
import com.mickels.freelancertoolsservice.application.port.out.ClientRepository;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Client;

import java.util.List;
import java.util.UUID;

/** Application service for client management (FR-001). Framework-free. */
public class ManageClientService implements ManageClientUseCase {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

    public ManageClientService(ClientRepository clientRepository, ProjectRepository projectRepository) {
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public Client create(ClientCommand command) {
        return clientRepository.save(Client.create(command.name(), command.contactEmail(), command.notes()));
    }

    @Override
    public Client update(UUID id, ClientCommand command) {
        Client existing = get(id);
        return clientRepository.save(existing.withDetails(command.name(), command.contactEmail(), command.notes()));
    }

    @Override
    public Client get(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id));
    }

    @Override
    public List<Client> list() {
        return clientRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client", id);
        }
        if (projectRepository.existsByClientId(id)) {
            throw new DependentRecordsException("Client " + id + " has projects and cannot be deleted");
        }
        clientRepository.deleteById(id);
    }
}
