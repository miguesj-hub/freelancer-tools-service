package com.mickels.freelancertoolsservice.domain;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.model.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientTest {

    @Test
    @DisplayName("Given valid details, when creating a client, then it has no persisted identity yet")
    void createsUnsavedClient() {
        Client client = Client.create("Acme Studio", "hi@acme.io", "vip");

        assertThat(client.getId()).isNull();
        assertThat(client.getCreatedAt()).isNull();
        assertThat(client.getName()).isEqualTo("Acme Studio");
        assertThat(client.getContactEmail()).isEqualTo("hi@acme.io");
        assertThat(client.getNotes()).isEqualTo("vip");
    }

    @Test
    @DisplayName("Given a blank name, when creating a client, then validation fails")
    void rejectsBlankName() {
        assertThatThrownBy(() -> Client.create("  ", null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given a null name, when creating a client, then validation fails")
    void rejectsNullName() {
        assertThatThrownBy(() -> Client.create(null, null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given an over-long name, when creating a client, then validation fails")
    void rejectsLongName() {
        String longName = "x".repeat(121);
        assertThatThrownBy(() -> Client.create(longName, null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given an existing client, when updating details, then identity and creation time are preserved")
    void withDetailsPreservesIdentity() {
        UUID id = UUID.randomUUID();
        Instant created = Instant.now();
        Client client = new Client(id, "Old", null, null, created);

        Client updated = client.withDetails("New", "n@x.io", "note");

        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getCreatedAt()).isEqualTo(created);
        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getContactEmail()).isEqualTo("n@x.io");
        assertThat(updated.getNotes()).isEqualTo("note");
    }
}
