package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProjectPersistenceAdapter.class)
class ProjectPersistenceAdapterTest {

    @Autowired
    ProjectPersistenceAdapter adapter;

    @Test
    @DisplayName("Given projects for a client, when queried by client, then only that client's projects return")
    void findsByClient() {
        UUID clientA = UUID.randomUUID();
        UUID clientB = UUID.randomUUID();
        Project saved = adapter.save(Project.create(clientA, "Website", null, null));
        adapter.save(Project.create(clientB, "Other", null, null));

        assertThat(adapter.findByClientId(clientA)).hasSize(1);
        assertThat(adapter.existsByClientId(clientA)).isTrue();
        assertThat(adapter.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Given a project with notes, when saved and re-read, then the notes round-trip (FR-016)")
    void roundTripsNotes() {
        Project saved = adapter.save(Project.create(UUID.randomUUID(), "Website", null, "kickoff Monday"));
        assertThat(adapter.findById(saved.getId())).isPresent()
                .get().extracting(Project::getNotes).isEqualTo("kickoff Monday");
    }

    @Test
    @DisplayName("Given a saved project, when deleted, then it no longer exists")
    void deletes() {
        Project saved = adapter.save(Project.create(UUID.randomUUID(), "P", null, null));
        adapter.deleteById(saved.getId());
        assertThat(adapter.existsById(saved.getId())).isFalse();
    }
}
