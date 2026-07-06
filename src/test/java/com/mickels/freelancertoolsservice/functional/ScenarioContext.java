package com.mickels.freelancertoolsservice.functional;

import org.springframework.http.ResponseEntity;

/** Per-scenario mutable state shared across step definitions (a scenario-scoped bean). */
public class ScenarioContext {

    public String clientId;
    public String projectId;
    public String taskId;
    public String lastTimeEntryId;
    public ResponseEntity<String> lastResponse;
}
