package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.application.port.in.ManageProjectUseCase;
import com.mickels.freelancertoolsservice.domain.model.Project;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ManageProjectUseCase useCase;

    private Project sample(UUID clientId) {
        return new Project(UUID.randomUUID(), clientId, "Website", "d", "n", Instant.now());
    }

    @Test
    void createsProject() throws Exception {
        UUID clientId = UUID.randomUUID();
        when(useCase.create(any(), any())).thenReturn(sample(clientId));
        mvc.perform(post("/clients/{clientId}/projects", clientId)
                        .contentType("application/json").content("{\"name\":\"Website\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Website"));
    }

    @Test
    void createsProjectWithNotes() throws Exception {
        UUID clientId = UUID.randomUUID();
        when(useCase.create(any(), any())).thenReturn(sample(clientId));
        mvc.perform(post("/clients/{clientId}/projects", clientId)
                        .contentType("application/json").content("{\"name\":\"Website\",\"notes\":\"n\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notes").value("n"));
    }

    @Test
    void getsProject() throws Exception {
        Project p = sample(UUID.randomUUID());
        when(useCase.get(any())).thenReturn(p);
        mvc.perform(get("/projects/{id}", p.getId())).andExpect(status().isOk());
    }

    @Test
    void listsProjectsByClient() throws Exception {
        UUID clientId = UUID.randomUUID();
        when(useCase.listByClient(any())).thenReturn(List.of(sample(clientId)));
        mvc.perform(get("/clients/{clientId}/projects", clientId)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Website"));
    }

    @Test
    void deletesProject() throws Exception {
        mvc.perform(delete("/projects/{id}", UUID.randomUUID())).andExpect(status().isNoContent());
    }
}
