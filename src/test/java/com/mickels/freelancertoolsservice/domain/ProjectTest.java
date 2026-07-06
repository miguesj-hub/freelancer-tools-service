package com.mickels.freelancertoolsservice.domain;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTest {

    @Test
    @DisplayName("Given a client and name, when creating a project, then it is linked to the client")
    void createsProject() {
        UUID clientId = UUID.randomUUID();
        Project project = Project.create(clientId, "Website", "redesign", null);

        assertThat(project.getClientId()).isEqualTo(clientId);
        assertThat(project.getName()).isEqualTo("Website");
        assertThat(project.getDescription()).isEqualTo("redesign");
        assertThat(project.getId()).isNull();
    }

    @Test
    @DisplayName("Given notes, when creating a project, then the notes are attached (FR-016)")
    void createsProjectWithNotes() {
        UUID clientId = UUID.randomUUID();
        Project project = Project.create(clientId, "Website", "redesign", "kickoff scheduled for Monday");

        assertThat(project.getNotes()).isEqualTo("kickoff scheduled for Monday");
    }

    @Test
    @DisplayName("Given no client, when creating a project, then validation fails")
    void rejectsNullClient() {
        assertThatThrownBy(() -> Project.create(null, "Website", null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given a blank name, when creating a project, then validation fails")
    void rejectsBlankName() {
        assertThatThrownBy(() -> Project.create(UUID.randomUUID(), " ", null, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given an over-long name, when creating a project, then validation fails")
    void rejectsLongName() {
        assertThatThrownBy(() -> Project.create(UUID.randomUUID(), "x".repeat(121), null, null))
                .isInstanceOf(ValidationException.class);
    }
}
