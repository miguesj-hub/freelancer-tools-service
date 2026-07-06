package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.domain.model.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ClientPersistenceAdapter.class)
class ClientPersistenceAdapterTest {

    @Autowired
    ClientPersistenceAdapter adapter;

    @Test
    @DisplayName("Given a new client, when saved, then it gets a UUID id and createdAt and is retrievable")
    void savesAndReads() {
        Client saved = adapter.save(Client.create("Acme", "e@x.io", "note"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(adapter.findById(saved.getId())).isPresent();
        assertThat(adapter.existsById(saved.getId())).isTrue();
        assertThat(adapter.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Given a saved client, when deleted, then it no longer exists")
    void deletes() {
        Client saved = adapter.save(Client.create("Acme", null, null));
        adapter.deleteById(saved.getId());
        assertThat(adapter.existsById(saved.getId())).isFalse();
    }
}
