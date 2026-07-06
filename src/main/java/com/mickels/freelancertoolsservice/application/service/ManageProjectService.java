package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ManageProjectUseCase;
import com.mickels.freelancertoolsservice.application.port.out.ClientRepository;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Project;

import java.util.List;
import java.util.UUID;

/** Application service for project management (FR-002). Framework-free. */
public class ManageProjectService implements ManageProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final TaskRepository taskRepository;

    public ManageProjectService(ProjectRepository projectRepository,
                                ClientRepository clientRepository,
                                TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Project create(UUID clientId, ProjectCommand command) {
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Client", clientId);
        }
        return projectRepository.save(Project.create(clientId, command.name(), command.description(), command.notes()));
    }

    @Override
    public Project get(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project", id));
    }

    @Override
    public List<Project> listByClient(UUID clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Client", clientId);
        }
        return projectRepository.findByClientId(clientId);
    }

    @Override
    public void delete(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project", id);
        }
        if (taskRepository.existsByProjectId(id)) {
            throw new DependentRecordsException("Project " + id + " has tasks and cannot be deleted");
        }
        projectRepository.deleteById(id);
    }
}
