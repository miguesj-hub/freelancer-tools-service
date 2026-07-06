package com.mickels.freelancertoolsservice.application.port.in;

import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;

import java.util.List;
import java.util.UUID;

/** Inbound port: manage tasks under a project (FR-003, FR-005..FR-007). */
public interface ManageTaskUseCase {

    Task create(UUID projectId, TaskCommand command);

    Task get(UUID id);

    List<Task> listByProject(UUID projectId);

    Task updateStatus(UUID id, TaskStatus status);

    void delete(UUID id);

    record TaskCommand(String title, String description) {}
}
