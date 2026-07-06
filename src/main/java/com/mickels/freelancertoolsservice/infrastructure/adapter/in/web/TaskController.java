package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.api.TasksApi;
import com.mickels.freelancertoolsservice.api.model.Task;
import com.mickels.freelancertoolsservice.api.model.TaskRequest;
import com.mickels.freelancertoolsservice.api.model.TaskStatusUpdate;
import com.mickels.freelancertoolsservice.application.port.in.ManageTaskUseCase;
import com.mickels.freelancertoolsservice.application.port.in.ManageTaskUseCase.TaskCommand;
import com.mickels.freelancertoolsservice.infrastructure.adapter.in.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController implements TasksApi {

    private final ManageTaskUseCase useCase;

    public TaskController(ManageTaskUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Task> createTask(String projectId, TaskRequest request) {
        var created = useCase.create(WebMapper.parseId(projectId),
                new TaskCommand(request.getTitle(), request.getDescription(), request.getNotes()));
        return ResponseEntity.status(HttpStatus.CREATED).body(WebMapper.toDto(created));
    }

    @Override
    public ResponseEntity<Task> getTask(String taskId) {
        return ResponseEntity.ok(WebMapper.toDto(useCase.get(WebMapper.parseId(taskId))));
    }

    @Override
    public ResponseEntity<List<Task>> listTasksByProject(String projectId) {
        return ResponseEntity.ok(useCase.listByProject(WebMapper.parseId(projectId))
                .stream().map(WebMapper::toDto).toList());
    }

    @Override
    public ResponseEntity<Task> updateTaskStatus(String taskId, TaskStatusUpdate request) {
        var updated = useCase.updateStatus(WebMapper.parseId(taskId), WebMapper.toDomain(request.getStatus()));
        return ResponseEntity.ok(WebMapper.toDto(updated));
    }

    @Override
    public ResponseEntity<Void> deleteTask(String taskId) {
        useCase.delete(WebMapper.parseId(taskId));
        return ResponseEntity.noContent().build();
    }
}
