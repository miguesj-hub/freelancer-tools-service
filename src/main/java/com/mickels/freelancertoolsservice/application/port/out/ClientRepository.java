package com.mickels.freelancertoolsservice.application.port.out;

import com.mickels.freelancertoolsservice.domain.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Outbound port for client persistence (implemented in the infrastructure layer). */
public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(UUID id);

    List<Client> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
