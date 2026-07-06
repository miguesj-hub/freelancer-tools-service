package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.application.port.in.ManageTaskUseCase;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.vo.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ManageTaskUseCase useCase;

    private Task sample(TaskStatus status) {
        return new Task(UUID.randomUUID(), UUID.randomUUID(), "Design", "d", "n", status, Instant.now());
    }

    @Test
    void createsTask() throws Exception {
        when(useCase.create(any(), any())).thenReturn(sample(TaskStatus.TO_DO));
        mvc.perform(post("/projects/{projectId}/tasks", UUID.randomUUID())
                        .contentType("application/json").content("{\"title\":\"Design\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TO_DO"));
    }

    @Test
    void getsTask() throws Exception {
        Task t = sample(TaskStatus.TO_DO);
        when(useCase.get(any())).thenReturn(t);
        mvc.perform(get("/tasks/{id}", t.getId())).andExpect(status().isOk());
    }

    @Test
    void listsTasksByProject() throws Exception {
        when(useCase.listByProject(any())).thenReturn(List.of(sample(TaskStatus.TO_DO)));
        mvc.perform(get("/projects/{projectId}/tasks", UUID.randomUUID())).andExpect(status().isOk());
    }

    @Test
    void createsTaskWithNotes() throws Exception {
        when(useCase.create(any(), any())).thenReturn(sample(TaskStatus.TO_DO));
        mvc.perform(post("/projects/{projectId}/tasks", UUID.randomUUID())
                        .contentType("application/json").content("{\"title\":\"Design\",\"notes\":\"n\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notes").value("n"));
    }

    @Test
    void updatesStatus() throws Exception {
        when(useCase.updateStatus(any(), any())).thenReturn(sample(TaskStatus.IN_PROGRESS));
        mvc.perform(patch("/tasks/{id}/status", UUID.randomUUID())
                        .contentType("application/json").content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Given an unknown status value, when updating a task, then it is rejected with 400 (FR-005)")
    void rejectsUnknownStatusValue() throws Exception {
        mvc.perform(patch("/tasks/{id}/status", UUID.randomUUID())
                        .contentType("application/json").content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletesTask() throws Exception {
        mvc.perform(delete("/tasks/{id}", UUID.randomUUID())).andExpect(status().isNoContent());
    }
}
