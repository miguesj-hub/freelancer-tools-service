package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.api.ClientsApi;
import com.mickels.freelancertoolsservice.api.model.Client;
import com.mickels.freelancertoolsservice.api.model.ClientRequest;
import com.mickels.freelancertoolsservice.application.port.in.ManageClientUseCase;
import com.mickels.freelancertoolsservice.application.port.in.ManageClientUseCase.ClientCommand;
import com.mickels.freelancertoolsservice.infrastructure.adapter.in.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController implements ClientsApi {

    private final ManageClientUseCase useCase;

    public ClientController(ManageClientUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Client> createClient(ClientRequest request) {
        var created = useCase.create(new ClientCommand(request.getName(), request.getContactEmail(), request.getNotes()));
        return ResponseEntity.status(HttpStatus.CREATED).body(WebMapper.toDto(created));
    }

    @Override
    public ResponseEntity<Client> getClient(String clientId) {
        return ResponseEntity.ok(WebMapper.toDto(useCase.get(WebMapper.parseId(clientId))));
    }

    @Override
    public ResponseEntity<List<Client>> listClients() {
        return ResponseEntity.ok(useCase.list().stream().map(WebMapper::toDto).toList());
    }

    @Override
    public ResponseEntity<Client> updateClient(String clientId, ClientRequest request) {
        var updated = useCase.update(WebMapper.parseId(clientId),
                new ClientCommand(request.getName(), request.getContactEmail(), request.getNotes()));
        return ResponseEntity.ok(WebMapper.toDto(updated));
    }

    @Override
    public ResponseEntity<Void> deleteClient(String clientId) {
        useCase.delete(WebMapper.parseId(clientId));
        return ResponseEntity.noContent().build();
    }
}
