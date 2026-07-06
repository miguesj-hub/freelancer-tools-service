package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.application.port.out.ClientRepository;
import com.mickels.freelancertoolsservice.domain.model.Client;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClientPersistenceAdapter implements ClientRepository {

    private final ClientJpaRepository jpa;

    public ClientPersistenceAdapter(ClientJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Client save(Client client) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(client)));
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpa.findAll().stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}
