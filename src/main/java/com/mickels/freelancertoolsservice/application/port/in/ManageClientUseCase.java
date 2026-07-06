package com.mickels.freelancertoolsservice.application.port.in;

import com.mickels.freelancertoolsservice.domain.model.Client;

import java.util.List;
import java.util.UUID;

/** Inbound port: manage clients (FR-001). */
public interface ManageClientUseCase {

    Client create(ClientCommand command);

    Client update(UUID id, ClientCommand command);

    Client get(UUID id);

    List<Client> list();

    void delete(UUID id);

    record ClientCommand(String name, String contactEmail, String notes) {}
}
