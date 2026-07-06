package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.ManageTaskUseCase;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;

import java.util.List;
import java.util.UUID;

/** Application service for task management (FR-003, FR-005..FR-007). Framework-free. */
public class ManageTaskService implements ManageTaskUseCase {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TimeEntryRepository timeEntryRepository;

    public ManageTaskService(TaskRepository taskRepository,
                             ProjectRepository projectRepository,
                             TimeEntryRepository timeEntryRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.timeEntryRepository = timeEntryRepository;
    }

    @Override
    public Task create(UUID projectId, TaskCommand command) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project", projectId);
        }
        return taskRepository.save(Task.create(projectId, command.title(), command.description(), command.notes()));
    }

    @Override
    public Task get(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));
    }

    @Override
    public List<Task> listByProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project", projectId);
        }
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    public Task updateStatus(UUID id, TaskStatus status) {
        Task existing = get(id);
        return taskRepository.save(existing.withStatus(status));
    }

    @Override
    public void delete(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task", id);
        }
        if (timeEntryRepository.existsByTaskId(id)) {
            throw new DependentRecordsException("Task " + id + " has time entries and cannot be deleted");
        }
        taskRepository.deleteById(id);
    }
}
