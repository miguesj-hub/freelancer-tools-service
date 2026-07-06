package com.mickels.freelancertoolsservice.domain;

import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskTest {

    @Test
    @DisplayName("Given a project, when creating a task, then it defaults to TO_DO")
    void defaultsToToDo() {
        Task task = Task.create(UUID.randomUUID(), "Design homepage", null);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TO_DO);
    }

    @Test
    @DisplayName("Given a null status in the constructor, when building a task, then it defaults to TO_DO")
    void nullStatusDefaults() {
        Task task = new Task(null, UUID.randomUUID(), "t", null, null, null);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TO_DO);
    }

    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    @DisplayName("Given a task, when changing to any valid status, then the new status is applied")
    void changesToAnyStatus(TaskStatus target) {
        Task task = Task.create(UUID.randomUUID(), "t", null);
        assertThat(task.withStatus(target).getStatus()).isEqualTo(target);
    }

    @Test
    @DisplayName("Given a task, when changing status to null, then validation fails")
    void rejectsNullStatus() {
        Task task = Task.create(UUID.randomUUID(), "t", null);
        assertThatThrownBy(() -> task.withStatus(null)).isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given no project, when creating a task, then validation fails")
    void rejectsNullProject() {
        assertThatThrownBy(() -> Task.create(null, "t", null)).isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given a blank title, when creating a task, then validation fails")
    void rejectsBlankTitle() {
        assertThatThrownBy(() -> Task.create(UUID.randomUUID(), " ", null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given an over-long title, when creating a task, then validation fails")
    void rejectsLongTitle() {
        assertThatThrownBy(() -> Task.create(UUID.randomUUID(), "x".repeat(201), null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Given a task, when changing status, then identity is preserved")
    void preservesIdentityOnStatusChange() {
        UUID id = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Task task = new Task(id, projectId, "t", "d", TaskStatus.TO_DO, null);
        Task moved = task.withStatus(TaskStatus.DONE);
        assertThat(moved.getId()).isEqualTo(id);
        assertThat(moved.getProjectId()).isEqualTo(projectId);
        assertThat(moved.getDescription()).isEqualTo("d");
    }
}
