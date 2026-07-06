package com.mickels.freelancertoolsservice.functional.steps;

import com.mickels.freelancertoolsservice.functional.HubClient;
import com.mickels.freelancertoolsservice.functional.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps for classification and the hours report (US3). */
public class HoursReportSteps {

    private final HubClient api;
    private final ScenarioContext ctx;

    public HoursReportSteps(HubClient api, ScenarioContext ctx) {
        this.api = api;
        this.ctx = ctx;
    }

    @When("I reclassify the last time entry as {string}")
    public void iReclassifyTheLastTimeEntryAs(String type) {
        ctx.lastResponse = api.patch("/time-entries/" + ctx.lastTimeEntryId + "/classification",
                Map.of("type", type));
        assertThat(ctx.lastResponse.getStatusCode().value()).isEqualTo(200);
    }

    @Then("the hours report for the client shows {int} billable and {int} administrative")
    public void theHoursReportShows(int billable, int administrative) {
        var response = api.get("/reports/hours?clientId=" + ctx.clientId);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(api.number(response, "billableMinutes")).isEqualTo(billable);
        assertThat(api.number(response, "administrativeMinutes")).isEqualTo(administrative);
        assertThat(api.number(response, "totalMinutes")).isEqualTo(billable + administrative);
    }
}
