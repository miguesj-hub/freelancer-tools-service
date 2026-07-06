package com.mickels.freelancertoolsservice.functional.steps;

import com.mickels.freelancertoolsservice.functional.HubClient;
import com.mickels.freelancertoolsservice.functional.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for clients, projects and tasks (US1) plus shared assertions. */
public class WorkHubSteps {

    private final HubClient api;
    private final ScenarioContext ctx;

    public WorkHubSteps(HubClient api, ScenarioContext ctx) {
        this.api = api;
        this.ctx = ctx;
    }

    @Given("a client {string}")
    public void aClient(String name) {
        ctx.lastResponse = api.post("/clients", Map.of("name", name));
        assertThat(ctx.lastResponse.getStatusCode().value()).isEqualTo(201);
        ctx.clientId = api.field(ctx.lastResponse, "id");
    }

    @Given("a project {string} for that client")
    public void aProjectForThatClient(String name) {
        ctx.lastResponse = api.post("/clients/" + ctx.clientId + "/projects", Map.of("name", name));
        assertThat(ctx.lastResponse.getStatusCode().value()).isEqualTo(201);
        ctx.projectId = api.field(ctx.lastResponse, "id");
    }

    @Given("a task {string} for that project")
    public void aTaskForThatProject(String title) {
        ctx.lastResponse = api.post("/projects/" + ctx.projectId + "/tasks", Map.of("title", title));
        assertThat(ctx.lastResponse.getStatusCode().value()).isEqualTo(201);
        ctx.taskId = api.field(ctx.lastResponse, "id");
    }

    @When("I set the task status to {string}")
    public void iSetTheTaskStatusTo(String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        ctx.lastResponse = api.patch("/tasks/" + ctx.taskId + "/status", body);
    }

    @When("I delete the client")
    public void iDeleteTheClient() {
        ctx.lastResponse = api.delete("/clients/" + ctx.clientId);
    }

    @Then("the task is stored with status {string}")
    public void theTaskIsStoredWithStatus(String status) {
        var response = api.get("/tasks/" + ctx.taskId);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(api.field(response, "status")).isEqualTo(status);
        assertThat(api.field(response, "projectId")).isEqualTo(ctx.projectId);
    }

    @Then("the request is rejected with status {int}")
    public void theRequestIsRejectedWithStatus(int status) {
        assertThat(ctx.lastResponse.getStatusCode().value()).isEqualTo(status);
    }
}
