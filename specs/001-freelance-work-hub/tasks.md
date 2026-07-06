---
description: "Task list for Freelance Work Hub — Clients, Projects, Tasks & Time"
---

# Tasks: Freelance Work Hub — Clients, Projects, Tasks & Time

**Input**: Design documents from `/specs/001-freelance-work-hub/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/freelancer-tools-api.yaml

**Tests**: INCLUDED — the project constitution (Principle II) makes BDD unit, integration and
functional tests mandatory, and (Principle V) enforces JaCoCo coverage gates (per-class > 80%,
global ≥ 80%). Test tasks are therefore first-class, not optional.

**Architecture**: Clean Architecture — dependencies point inward
(`infrastructure` → `application` → `domain`). Domain has no Spring/JPA. API is contract-first:
openapi-generator produces `*Api` interfaces + DTOs into `build/generated`; controllers implement
them (never hand-edit generated code).

**Base package**: `com.mickels.freelancertoolsservice` (paths below abbreviate it as
`src/main/java/.../` and `src/test/java/.../`).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: US1, US2, US3 (Setup/Foundational/Polish carry no story label)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Wire the mandated stack, contract generation, and coverage gates.

- [ ] T001 Update `build.gradle` dependencies: add `spring-boot-starter-web`,
  `spring-boot-starter-validation`, `spring-boot-starter-test` (JUnit5, Mockito, AssertJ),
  `io.cucumber:cucumber-java` + `cucumber-spring` + `cucumber-junit-platform-engine`, and
  `org.junit.platform:junit-platform-suite` for the Cucumber runner.
- [ ] T002 Add the `org.openapi.generator` Gradle plugin in `build.gradle`; copy the contract to
  `src/main/resources/openapi/freelancer-tools-api.yaml`; configure an `openApiGenerate` task
  (`generatorName=spring`, `interfaceOnly=true`, `useSpringBoot3=true`, apiPackage
  `com.mickels.freelancertoolsservice.api`, modelPackage `...api.model`, output `build/generated`)
  and add the generated sources to the main source set + make `compileJava` depend on it.
- [ ] T003 Apply the `jacoco` plugin in `build.gradle`: configure `jacocoTestReport` (HTML+XML) and
  `jacocoTestCoverageVerification` with a BUNDLE rule ≥ 0.80 and a CLASS rule > 0.80, excluding
  generated OpenAPI classes (`**/api/**`), `**/*Application*`, and config boilerplate; make
  `check` depend on the verification task.
- [ ] T004 [P] Configure `src/main/resources/application.yaml` for H2 (datasource + JPA `ddl-auto`)
  and enable the H2 console.
- [ ] T005 [P] Create the Clean Architecture package skeleton under
  `src/main/java/.../` (`domain/model`, `domain/vo`, `domain/exception`, `application/port/in`,
  `application/port/out`, `application/service`, `infrastructure/adapter/in/web`,
  `infrastructure/adapter/in/web/mapper`, `infrastructure/adapter/out/persistence`,
  `infrastructure/adapter/out/persistence/mapper`, `infrastructure/config`).

**Checkpoint**: `./gradlew clean build` compiles (generated interfaces present, gates wired).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Cross-cutting infrastructure required by every user story.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T006 [P] Create domain exception types in `src/main/java/.../domain/exception/`:
  `DomainException`, `EntityNotFoundException`, `DependentRecordsException` (for FR-013 conflicts),
  `ValidationException`.
- [ ] T007 Create global exception handling in
  `src/main/java/.../infrastructure/config/GlobalExceptionHandler.java` (`@RestControllerAdvice`)
  mapping `EntityNotFoundException`→404, `DependentRecordsException`→409, bean-validation +
  `ValidationException`→400, all serialized to the generated `ProblemDetail` DTO.
- [ ] T008 [P] Create a shared timestamp/identity strategy in
  `src/main/java/.../infrastructure/adapter/out/persistence/` (base JPA `@MappedSuperclass` with
  a `UUID` id generated via `@GeneratedValue(strategy = UUID)` + `createdAt` set on `@PrePersist`)
  reused by all persistence entities. Domain models carry the id as `UUID`; DTOs serialize it as string.

**Checkpoint**: Foundation ready — user stories can begin.

---

## Phase 3: User Story 1 - Register clients, projects and tasks in one place (Priority: P1) 🎯 MVP

**Goal**: Create a client, add projects to it, and create tasks (default state TO_DO) under
projects — entirely in the hub. Support task state changes among TO_DO/IN_PROGRESS/DONE.

**Independent Test**: Create a client with no tasks, add a project, create a task → task stored,
retrievable, linked to project+client, status defaults TO_DO; changing status persists.

### Tests for User Story 1 (write first, must FAIL before implementation) ⚠️

- [ ] T009 [P] [US1] Functional Gherkin scenarios in
  `src/test/resources/features/manage-clients-projects-tasks.feature` covering acceptance
  scenarios 1–4 (create client→project→task, default TO_DO, status change).
- [ ] T010 [P] [US1] Cucumber runner + Spring glue in
  `src/test/java/.../functional/CucumberTest.java` and `.../functional/steps/WorkHubSteps.java`
  (`@SpringBootTest` random port + REST client).
- [ ] T011 [P] [US1] Unit tests for domain in `src/test/java/.../domain/` (Given/When/Then):
  `ClientTest`, `ProjectTest`, `TaskTest` including default TO_DO and all pairwise status
  transitions, plus invalid/blank field rules.
- [ ] T012 [P] [US1] Unit tests for use-case services in
  `src/test/java/.../application/service/` with mocked repository ports: `ManageClientServiceTest`,
  `ManageProjectServiceTest` (rejects non-existent client), `ManageTaskServiceTest` (rejects
  non-existent project; delete blocked while dependents exist).
- [ ] T013 [P] [US1] `@DataJpaTest` integration tests in
  `src/test/java/.../infrastructure/adapter/out/persistence/` for the client/project/task
  persistence adapters against H2.

### Implementation for User Story 1

- [ ] T014 [P] [US1] `TaskStatus` enum in `src/main/java/.../domain/vo/TaskStatus.java`
  (TO_DO, IN_PROGRESS, DONE).
- [ ] T015 [P] [US1] `Client` domain model in `src/main/java/.../domain/model/Client.java`.
- [ ] T016 [P] [US1] `Project` domain model in `src/main/java/.../domain/model/Project.java`
  (holds clientId).
- [ ] T017 [P] [US1] `Task` domain model in `src/main/java/.../domain/model/Task.java`
  (holds projectId, status; factory defaults TO_DO; `changeStatus` behavior).
- [ ] T018 [P] [US1] Inbound port interfaces in `src/main/java/.../application/port/in/`:
  `ManageClientUseCase`, `ManageProjectUseCase`, `ManageTaskUseCase`.
- [ ] T019 [P] [US1] Outbound port interfaces in `src/main/java/.../application/port/out/`:
  `ClientRepository`, `ProjectRepository`, `TaskRepository`.
- [ ] T020 [US1] `ManageClientService` in `src/main/java/.../application/service/` (create/read/
  update/list; delete blocked when projects exist → `DependentRecordsException`).
- [ ] T021 [US1] `ManageProjectService` in `src/main/java/.../application/service/` (validates
  client exists; delete blocked when tasks exist). Depends on T019, T020.
- [ ] T022 [US1] `ManageTaskService` in `src/main/java/.../application/service/` (validates project
  exists; default TO_DO; status change; delete blocked when time entries exist). Depends on T019.
- [ ] T023 [US1] JPA entities + Spring Data repositories + persistence adapters + mappers for
  Client/Project/Task in `src/main/java/.../infrastructure/adapter/out/persistence/`
  (implements the outbound ports). Depends on T008, T015–T017, T019.
- [ ] T024 [US1] Web controllers implementing generated `ClientsApi`, `ProjectsApi`, `TasksApi` in
  `src/main/java/.../infrastructure/adapter/in/web/` + DTO⇄domain mappers in `.../web/mapper/`
  (endpoints: clients CRUD, projects create/list/get/delete, tasks create/list/get/delete,
  `PATCH /tasks/{id}/status`). Depends on T018, T020–T022.

**Checkpoint**: US1 fully functional and independently testable (MVP).

---

## Phase 4: User Story 2 - Log time with permanent client/project/task association (Priority: P2)

**Goal**: Log time entries (minutes > 0, workDate) against a task; each is permanently tied to
task/project/client; a task may hold many entries. New entries default to type BILLABLE.

**Independent Test**: With an existing task, log a time entry → stored with task/project/client;
changing the task status leaves associations unchanged; multiple entries coexist.

### Tests for User Story 2 (write first, must FAIL before implementation) ⚠️

- [ ] T025 [P] [US2] Functional Gherkin scenarios in
  `src/test/resources/features/log-time.feature` (log time on a TO_DO task; associations stable
  after status change; reject minutes ≤ 0; reject non-existent task; **multiple entries on one
  task all persist and list — FR-010**).
- [ ] T026 [P] [US2] Cucumber step definitions for time logging in
  `src/test/java/.../functional/steps/TimeLoggingSteps.java`.
- [ ] T027 [P] [US2] Unit tests in `src/test/java/.../domain/TimeEntryTest.java` (duration > 0,
  immutable associations) and `src/test/java/.../application/service/LogTimeServiceTest.java`
  (resolves project+client from task; rejects invalid task/duration; **logging multiple entries
  against the same task stores each independently and all are retrievable — FR-010**).
- [ ] T028 [P] [US2] `@DataJpaTest` integration test for the time-entry persistence adapter in
  `src/test/java/.../infrastructure/adapter/out/persistence/TimeEntryPersistenceAdapterTest.java`.

### Implementation for User Story 2

- [ ] T029 [P] [US2] `TimeEntryType` enum in `src/main/java/.../domain/vo/TimeEntryType.java`
  (BILLABLE, ADMINISTRATIVE).
- [ ] T030 [P] [US2] `TimeEntry` domain model in `src/main/java/.../domain/model/TimeEntry.java`
  (taskId/projectId/clientId immutable, minutes > 0 invariant, default BILLABLE).
- [ ] T031 [P] [US2] `LogTimeUseCase` inbound port in `src/main/java/.../application/port/in/` and
  `TimeEntryRepository` outbound port in `src/main/java/.../application/port/out/`.
- [ ] T032 [US2] `LogTimeService` in `src/main/java/.../application/service/` (loads task, derives
  project+client, validates duration, defaults type BILLABLE, lists entries by task). Depends on
  T019 (TaskRepository), T031.
- [ ] T033 [US2] JPA entity + Spring Data repository + persistence adapter + mapper for TimeEntry
  in `src/main/java/.../infrastructure/adapter/out/persistence/`. Depends on T008, T030, T031.
- [ ] T034 [US2] Web controller implementing generated `TimeEntriesApi` (log + list by task) +
  DTO⇄domain mapper in `src/main/java/.../infrastructure/adapter/in/web/`. Depends on T031, T032.

**Checkpoint**: US1 and US2 both independently functional.

---

## Phase 5: User Story 3 - Classify time and hours report (Priority: P3)

**Goal**: Re-classify a time entry (BILLABLE/ADMINISTRATIVE) and produce an hours report that
totals minutes by type, scopable to client/project/task.

**Independent Test**: Mark one entry BILLABLE and another ADMINISTRATIVE under the same task;
request the hours report → billable and administrative minutes reported separately.

### Tests for User Story 3 (write first, must FAIL before implementation) ⚠️

- [ ] T035 [P] [US3] Functional Gherkin scenarios in
  `src/test/resources/features/hours-report.feature` (classify entries; report groups by type;
  same task contributes both types).
- [ ] T036 [P] [US3] Cucumber step definitions in
  `src/test/java/.../functional/steps/HoursReportSteps.java`.
- [ ] T037 [P] [US3] Unit tests in `src/test/java/.../application/service/`:
  `ClassifyTimeEntryServiceTest` and `HoursReportServiceTest` (aggregation by type + scope
  filtering).
- [ ] T038 [P] [US3] `@DataJpaTest` integration test for the aggregation query in
  `src/test/java/.../infrastructure/adapter/out/persistence/HoursReportQueryTest.java`.

### Implementation for User Story 3

- [ ] T039 [P] [US3] `HoursReport` derived model in `src/main/java/.../domain/model/HoursReport.java`
  (billable/administrative/total minutes + scope).
- [ ] T040 [P] [US3] `ClassifyTimeEntryUseCase` and `HoursReportUseCase` inbound ports in
  `src/main/java/.../application/port/in/`.
- [ ] T041 [US3] Add aggregation methods (sum minutes by type, filtered by client/project/task) to
  `TimeEntryRepository` port and its persistence adapter in
  `src/main/java/.../infrastructure/adapter/out/persistence/`. Depends on T031, T033.
- [ ] T042 [US3] `ClassifyTimeEntryService` and `HoursReportService` in
  `src/main/java/.../application/service/`. Depends on T031, T040, T041.
- [ ] T043 [US3] Web controllers: `PATCH /time-entries/{id}/classification` (extend TimeEntries
  controller) and a controller implementing generated `ReportsApi` for `GET /reports/hours`, in
  `src/main/java/.../infrastructure/adapter/in/web/`. Depends on T040, T042.

**Checkpoint**: All three user stories independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Optional scope and quality closure.

- [ ] T044 [P] [Optional — FR-016] Notes capability: `Note` domain model, `ManageNoteUseCase`/
  `NoteRepository` ports, service, persistence adapter, and controller for attaching notes to
  client/project/task. Deliver only if effort allows (does not block MVP).
- [ ] T045 [P] Run `./gradlew jacocoTestReport jacocoTestCoverageVerification`; add targeted tests
  to close any class below 80% or global below 80%.
- [ ] T046 [P] Execute `specs/001-freelance-work-hub/quickstart.md` end-to-end against `bootRun`
  and confirm all validation-checklist items pass.
- [ ] T047 Review for SOLID/DRY/YAGNI + Clean Architecture boundary violations (no framework
  imports in `domain`/`application`, no hand-edited generated code); refactor as needed. Confirm
  FR-015: no synchronization/import/export endpoints or clients to Trello/Toggl/Notion/Sheets
  exist in the API surface or dependencies.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately.
- **Foundational (Phase 2)**: Depends on Setup — BLOCKS all user stories.
- **User Stories (Phases 3–5)**: All depend on Foundational.
  - US1 (P1) has no dependency on other stories.
  - US2 (P2) depends on US1's `TaskRepository`/Task (needs an existing task to log against).
  - US3 (P3) depends on US2's `TimeEntry`/`TimeEntryRepository` (classifies + aggregates entries).
- **Polish (Phase 6)**: Depends on the user stories it touches.

### Story Completion Order

Foundational → US1 → US2 → US3 (strict, because the hierarchy nests: time needs tasks; reports
need time). Within a story, tests are written first and must fail before implementation.

### Within Each User Story

- Tests → domain models → ports → services → persistence adapters → web controllers.
- Domain before services; services before persistence wiring/controllers.

### Parallel Opportunities

- Setup: T004, T005 parallel; Foundational: T006, T008 parallel.
- US1: all test tasks T009–T013 parallel; domain T014–T017 parallel; ports T018–T019 parallel.
- US2: tests T025–T028 parallel; T029–T031 parallel.
- US3: tests T035–T038 parallel; T039–T040 parallel.

---

## Parallel Example: User Story 1

```bash
# Write all US1 tests first (they must fail):
Task: "T009 Gherkin scenarios in src/test/resources/features/manage-clients-projects-tasks.feature"
Task: "T011 Domain unit tests in src/test/java/.../domain/"
Task: "T012 Use-case service unit tests in src/test/java/.../application/service/"
Task: "T013 @DataJpaTest persistence tests in src/test/java/.../infrastructure/adapter/out/persistence/"

# Then build domain + ports in parallel:
Task: "T014 TaskStatus enum"; Task: "T015 Client model"; Task: "T016 Project model"; Task: "T017 Task model"
Task: "T018 inbound ports"; Task: "T019 outbound ports"
```

---

## Implementation Strategy

### MVP First (User Story 1 only)

1. Phase 1 Setup → 2. Phase 2 Foundational → 3. Phase 3 US1 → **STOP & validate US1 independently**
   → deploy/demo. This already delivers the "single place for clients/projects/tasks" value.

### Incremental Delivery

Foundation → US1 (MVP) → US2 (time logging) → US3 (classification + reports) → Polish. Each story
is a shippable increment validated by its own Cucumber scenarios and the coverage gate.

---

## Notes

- [P] = different files, no dependency on an incomplete task.
- Every task lists an exact path; generated `*Api` interfaces live in `build/generated` and are
  implemented, never edited (Principle IV).
- Verify tests fail before implementing; keep every class above the 80% coverage gate (Principle V).
- Commit after each task or logical group.
