package com.mickels.freelancertoolsservice.application.port.in;

import com.mickels.freelancertoolsservice.domain.model.Project;

import java.util.List;
import java.util.UUID;

/** Inbound port: manage projects under a client (FR-002). */
public interface ManageProjectUseCase {

    Project create(UUID clientId, ProjectCommand command);

    Project get(UUID id);

    List<Project> listByClient(UUID clientId);

    void delete(UUID id);

    record ProjectCommand(String name, String description, String notes) {}
}
