package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.api.ProjectsApi;
import com.mickels.freelancertoolsservice.api.model.Project;
import com.mickels.freelancertoolsservice.api.model.ProjectRequest;
import com.mickels.freelancertoolsservice.application.port.in.ManageProjectUseCase;
import com.mickels.freelancertoolsservice.application.port.in.ManageProjectUseCase.ProjectCommand;
import com.mickels.freelancertoolsservice.infrastructure.adapter.in.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController implements ProjectsApi {

    private final ManageProjectUseCase useCase;

    public ProjectController(ManageProjectUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Project> createProject(String clientId, ProjectRequest request) {
        var created = useCase.create(WebMapper.parseId(clientId),
                new ProjectCommand(request.getName(), request.getDescription(), request.getNotes()));
        return ResponseEntity.status(HttpStatus.CREATED).body(WebMapper.toDto(created));
    }

    @Override
    public ResponseEntity<Project> getProject(String projectId) {
        return ResponseEntity.ok(WebMapper.toDto(useCase.get(WebMapper.parseId(projectId))));
    }

    @Override
    public ResponseEntity<List<Project>> listProjectsByClient(String clientId) {
        return ResponseEntity.ok(useCase.listByClient(WebMapper.parseId(clientId))
                .stream().map(WebMapper::toDto).toList());
    }

    @Override
    public ResponseEntity<Void> deleteProject(String projectId) {
        useCase.delete(WebMapper.parseId(projectId));
        return ResponseEntity.noContent().build();
    }
}
