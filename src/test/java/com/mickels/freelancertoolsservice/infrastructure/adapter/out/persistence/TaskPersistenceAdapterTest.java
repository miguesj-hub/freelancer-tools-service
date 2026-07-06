package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence;

import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TaskPersistenceAdapter.class)
class TaskPersistenceAdapterTest {

    @Autowired
    TaskPersistenceAdapter adapter;

    @Test
    @DisplayName("Given a task, when saved and re-read, then status round-trips and it lists by project")
    void savesAndReads() {
        UUID projectId = UUID.randomUUID();
        Task saved = adapter.save(Task.create(projectId, "Design", "desc"));

        assertThat(saved.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(adapter.findByProjectId(projectId)).hasSize(1);
        assertThat(adapter.existsByProjectId(projectId)).isTrue();

        Task moved = adapter.save(saved.withStatus(TaskStatus.DONE));
        assertThat(adapter.findById(moved.getId()).orElseThrow().getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    @DisplayName("Given a saved task, when deleted, then it no longer exists")
    void deletes() {
        Task saved = adapter.save(Task.create(UUID.randomUUID(), "t", null));
        adapter.deleteById(saved.getId());
        assertThat(adapter.existsById(saved.getId())).isFalse();
    }
}
