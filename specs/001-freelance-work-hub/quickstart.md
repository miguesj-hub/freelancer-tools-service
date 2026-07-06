# Quickstart & Validation Guide: Freelance Work Hub

This guide proves the feature works end-to-end. It is a run/validation reference — implementation
detail lives in `tasks.md` and the code.

## Prerequisites

- JDK 21 (the Gradle toolchain will resolve it)
- No external services — the app uses an in-memory/file H2 database
- The OpenAPI contract at `src/main/resources/openapi/freelancer-tools-api.yaml`
  (source of truth; copied from `specs/001-freelance-work-hub/contracts/freelancer-tools-api.yaml`)

## Build, generate, and enforce gates

```bash
# Generates OpenAPI server interfaces + DTOs, compiles, runs all tests,
# produces the JaCoCo report, and FAILS if coverage gates are not met.
./gradlew clean build

# Coverage report (after build):
#   build/reports/jacoco/test/html/index.html
# Gate: per-class > 80%, global >= 80% (jacocoTestCoverageVerification)
```

```bash
# Run the service locally
./gradlew bootRun
# API base path: http://localhost:8080/api/v1
# H2 console (dev): http://localhost:8080/h2-console
```

## Test suites (BDD, three levels)

```bash
./gradlew test                 # unit + integration (JUnit5, Given/When/Then) + Cucumber functional
```

- **Unit**: `src/test/java/.../domain`, `.../application/service` (ports mocked)
- **Integration**: `src/test/java/.../infrastructure/adapter/out/persistence` (`@DataJpaTest`, H2)
- **Functional**: Cucumber `.feature` files in `src/test/resources/features` driving `@SpringBootTest`

## End-to-end scenario walkthrough (maps to acceptance scenarios)

### US1 — Register a client, project and task (Acceptance Criterion 1)

```bash
# 1. Create a client (no tasks yet)
curl -s -X POST http://localhost:8080/api/v1/clients \
  -H 'Content-Type: application/json' \
  -d '{"name":"Acme Studio"}'
# -> 201, returns {id: <clientId>, ...}

# 2. Create a project under that client
curl -s -X POST http://localhost:8080/api/v1/clients/<clientId>/projects \
  -H 'Content-Type: application/json' \
  -d '{"name":"Website Redesign"}'
# -> 201, returns {id: <projectId>, clientId: <clientId>, ...}

# 3. Create a task under the project
curl -s -X POST http://localhost:8080/api/v1/projects/<projectId>/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Design homepage"}'
# -> 201, returns {id: <taskId>, status: "TO_DO", ...}   (default state)
```

**Expected**: Task stored and retrievable, linked to project + client, with zero external-tool
steps. `GET /tasks/<taskId>` returns status `TO_DO`.

### US2 — Log time permanently associated with client/project/task (Acceptance Criterion 2)

```bash
# Task is in TO_DO; log 90 minutes of work
curl -s -X POST http://localhost:8080/api/v1/tasks/<taskId>/time-entries \
  -H 'Content-Type: application/json' \
  -d '{"minutes":90,"workDate":"2026-07-05"}'
# -> 201, returns {id, taskId, projectId, clientId, minutes:90, type:"BILLABLE"}
```

**Expected**: The time entry carries taskId, projectId and clientId. Change the task status
(`PATCH /tasks/<taskId>/status`) and re-fetch the entry — its associations are unchanged.

**Negative check**: `minutes: 0` or negative → `400` ValidationError. Logging against a
non-existent task → `404`.

### US3 — Classify time and see it in the hours report (Acceptance Criterion 3)

```bash
# Add an administrative entry to the same task
curl -s -X POST http://localhost:8080/api/v1/tasks/<taskId>/time-entries \
  -H 'Content-Type: application/json' \
  -d '{"minutes":30,"workDate":"2026-07-05","type":"ADMINISTRATIVE"}'

# Hours report scoped to the client
curl -s "http://localhost:8080/api/v1/reports/hours?clientId=<clientId>"
# -> 200, {billableMinutes:90, administrativeMinutes:30, totalMinutes:120}
```

**Expected**: Billable and administrative hours are reported separately; the same task correctly
contributes hours of both types (classification lives on the entry).

## Success validation checklist

- [ ] `./gradlew clean build` passes, including `jacocoTestCoverageVerification`
- [ ] Coverage: per-class > 80% and global ≥ 80%
- [ ] Cucumber scenarios for US1–US3 all green and trace to the spec's acceptance scenarios
- [ ] Deleting a client/project/task with dependents returns `409` (no time-history loss)
- [ ] Orphan creation (bad clientId/projectId/taskId) returns `404`; bad duration returns `400`
