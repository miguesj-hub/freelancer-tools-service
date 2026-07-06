package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.application.port.in.ManageClientUseCase;
import com.mickels.freelancertoolsservice.domain.model.Client;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ManageClientUseCase useCase;

    private Client sample() {
        return new Client(UUID.randomUUID(), "Acme", "e@x.io", "n", Instant.now());
    }

    @Test
    void createsClient() throws Exception {
        when(useCase.create(any())).thenReturn(sample());
        mvc.perform(post("/clients").contentType("application/json").content("{\"name\":\"Acme\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Acme"));
    }

    @Test
    void getsClient() throws Exception {
        Client c = sample();
        when(useCase.get(any())).thenReturn(c);
        mvc.perform(get("/clients/{id}", c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c.getId().toString()));
    }

    @Test
    void listsClients() throws Exception {
        when(useCase.list()).thenReturn(List.of(sample()));
        mvc.perform(get("/clients")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Acme"));
    }

    @Test
    void updatesClient() throws Exception {
        when(useCase.update(any(), any())).thenReturn(sample());
        mvc.perform(put("/clients/{id}", UUID.randomUUID())
                        .contentType("application/json").content("{\"name\":\"New\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deletesClient() throws Exception {
        mvc.perform(delete("/clients/{id}", UUID.randomUUID())).andExpect(status().isNoContent());
    }
}
