package com.mickels.freelancertoolsservice.application.service;

import com.mickels.freelancertoolsservice.application.port.in.LogTimeUseCase;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.model.Project;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;

import java.util.List;
import java.util.UUID;

/**
 * Application service for logging time (FR-008..FR-011). Resolves the task's
 * project and client so the entry is permanently associated with all three (FR-009).
 */
public class LogTimeService implements LogTimeUseCase {

    private final TimeEntryRepository timeEntryRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public LogTimeService(TimeEntryRepository timeEntryRepository,
                          TaskRepository taskRepository,
                          ProjectRepository projectRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public TimeEntry log(UUID taskId, LogTimeCommand command) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task", taskId));
        Project project = projectRepository.findById(task.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project", task.getProjectId()));

        TimeEntry entry = TimeEntry.create(
                task.getId(),
                project.getId(),
                project.getClientId(),
                command.minutes(),
                command.workDate(),
                command.type(),
                command.description());
        return timeEntryRepository.save(entry);
    }

    @Override
    public List<TimeEntry> listByTask(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Task", taskId);
        }
        return timeEntryRepository.findByTaskId(taskId);
    }
}
