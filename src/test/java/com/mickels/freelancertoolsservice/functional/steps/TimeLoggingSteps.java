package com.mickels.freelancertoolsservice.functional.steps;

import com.mickels.freelancertoolsservice.functional.HubClient;
import com.mickels.freelancertoolsservice.functional.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for logging time against tasks (US2). */
public class TimeLoggingSteps {

    private final HubClient api;
    private final ScenarioContext ctx;

    public TimeLoggingSteps(HubClient api, ScenarioContext ctx) {
        this.api = api;
        this.ctx = ctx;
    }

    @When("I log {int} minutes on the task")
    public void iLogMinutesOnTheTask(int minutes) {
        logTime(ctx.taskId, minutes, null);
    }

    @When("I log {int} minutes on the task as {string}")
    public void iLogMinutesOnTheTaskAs(int minutes, String type) {
        logTime(ctx.taskId, minutes, type);
    }

    @When("I log {int} minutes on a random task")
    public void iLogMinutesOnARandomTask(int minutes) {
        logTime(UUID.randomUUID().toString(), minutes, null);
    }

    private void logTime(String taskId, int minutes, String type) {
        Map<String, Object> body = new HashMap<>();
        body.put("minutes", minutes);
        body.put("workDate", LocalDate.now().toString());
        if (type != null) {
            body.put("type", type);
        }
        ctx.lastResponse = api.post("/tasks/" + taskId + "/time-entries", body);
        if (ctx.lastResponse.getStatusCode().value() == 201) {
            ctx.lastTimeEntryId = api.field(ctx.lastResponse, "id");
        }
    }

    @Then("the time entry is associated with the task, project and client")
    public void theTimeEntryIsAssociated() {
        assertThat(ctx.lastResponse.getStatusCode().value()).isEqualTo(201);
        assertThat(api.field(ctx.lastResponse, "taskId")).isEqualTo(ctx.taskId);
        assertThat(api.field(ctx.lastResponse, "projectId")).isEqualTo(ctx.projectId);
        assertThat(api.field(ctx.lastResponse, "clientId")).isEqualTo(ctx.clientId);
    }

    @Then("the time entry still lists under the task")
    public void theTimeEntryStillListsUnderTheTask() {
        var response = api.get("/tasks/" + ctx.taskId + "/time-entries");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(api.arraySize(response)).isGreaterThanOrEqualTo(1);
    }

    @Then("the task has {int} time entries")
    public void theTaskHasTimeEntries(int count) {
        var response = api.get("/tasks/" + ctx.taskId + "/time-entries");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(api.arraySize(response)).isEqualTo(count);
    }
}
