# Implementation Plan: Freelance Work Hub — Clients, Projects, Tasks & Time

**Branch**: `001-freelance-work-hub` | **Date**: 2026-07-05 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-freelance-work-hub/spec.md`

## Summary

Deliver a self-contained hub where a freelancer manages clients, projects, tasks and time in
one place — no synchronization with Trello/Toggl/Notion/Sheets. The MVP exposes a REST API
(designed contract-first with OpenAPI) over a strict Client → Project → Task → Time Entry
hierarchy. Tasks move across three states (To Do / In Progress / Done); time entries are
permanently bound to their task/project/client and are classified as billable or
administrative, feeding an hours report. Implementation follows Clean Architecture with the
project's mandated stack (Java 21, Spring Boot, Spring Data JPA on H2, Lombok), BDD tests at
all levels, and JaCoCo coverage gates.

## Technical Context

**Language/Version**: Java 21 (Gradle toolchain-pinned)

**Primary Dependencies**: Spring Boot 4.1.x (web, validation), Spring Data JPA, Lombok,
openapi-generator (contract-first server interfaces + DTOs), JaCoCo (coverage), JUnit 5 +
Mockito + AssertJ (unit/integration), Cucumber-JVM (functional BDD)

**Storage**: H2 database via Spring Data JPA (in-memory for tests; file/in-memory for local run)

**Testing**: JUnit 5 (Given/When/Then-structured unit & integration tests), Spring Boot Test /
`@DataJpaTest` for persistence integration, Cucumber-JVM for functional end-to-end scenarios
against the API contract

**Target Platform**: JVM (Linux/macOS server), runnable as a standalone Spring Boot service

**Project Type**: Single-module web service (backend REST API)

**Performance Goals**: Interactive CRUD/reporting for a single freelancer; sub-second response
for typical operations. No high-throughput requirement in this story.

**Constraints**: No third-party synchronization (FR-015). Historical time data must never be
silently lost on delete (FR-013). Per-class coverage > 80%, global coverage ≥ 80% (JaCoCo gate).

**Scale/Scope**: Single-user data set; hundreds of clients/projects, thousands of tasks and
time entries — small-scale, no sharding/partitioning concerns.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Gate | Status |
|-----------|------|--------|
| I. Clean Architecture | Domain has zero framework deps; dependencies point inward via ports; JPA/web live in outer adapters | ✅ PASS — layered package layout below (domain / application / infrastructure) |
| II. BDD Testing | Unit + integration + functional tests, all Given/When/Then | ✅ PASS — JUnit5 (unit/integration) + Cucumber (functional) planned |
| III. SOLID / YAGNI / DRY | Use cases single-responsibility; ports invert deps; only specified scope built | ✅ PASS — no speculative entities/endpoints; MVP-only |
| IV. API First | OpenAPI contract committed; server generated with openapi-generator; no hand-editing generated code | ✅ PASS — contract at `src/main/resources/openapi/freelancer-tools-api.yaml`; generated interfaces implemented by controllers |
| V. Coverage Gates | JaCoCo per-class > 80%, global ≥ 80%, build-failing | ✅ PASS — `jacocoTestCoverageVerification` wired with thresholds; generated code excluded |

**Result**: PASS (no violations). Complexity Tracking not required.

## Project Structure

### Documentation (this feature)

```text
specs/001-freelance-work-hub/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (OpenAPI contract)
│   └── freelancer-tools-api.yaml
├── checklists/
│   └── requirements.md  # spec quality checklist (/speckit-specify)
└── tasks.md             # Phase 2 output (/speckit-tasks — NOT created here)
```

### Source Code (repository root)

Single-module Spring Boot service organized by Clean Architecture layers. Dependencies point
inward: `infrastructure` → `application` → `domain`. The domain layer has no Spring/JPA imports.

```text
src/main/
├── java/com/mickels/freelancertoolsservice/
│   ├── FreelancerToolsServiceApplication.java     # Spring Boot entry point (frameworks/drivers)
│   ├── domain/                                     # Enterprise business rules — pure Java
│   │   ├── model/                                  # Client, Project, Task, TimeEntry, Note
│   │   ├── vo/                                      # TaskStatus, TimeEntryType, Duration/Money VOs
│   │   └── exception/                               # DomainException, NotFoundException, etc.
│   ├── application/                                 # Application business rules (use cases)
│   │   ├── port/in/                                 # Use-case interfaces (ManageClientUseCase, LogTimeUseCase, HoursReportUseCase, ...)
│   │   ├── port/out/                                # Repository ports (ClientRepository, ProjectRepository, TaskRepository, TimeEntryRepository)
│   │   └── service/                                 # Use-case implementations (orchestrate domain + ports)
│   └── infrastructure/                              # Interface adapters + frameworks/drivers
│       ├── adapter/in/web/                          # REST controllers implementing generated API interfaces
│       │   └── mapper/                              # API DTO <-> domain mappers
│       ├── adapter/out/persistence/                 # JPA entities, Spring Data repositories, persistence adapters
│       │   └── mapper/                              # JPA entity <-> domain mappers
│       └── config/                                  # Spring configuration, exception handling, bean wiring
└── resources/
    ├── openapi/
    │   └── freelancer-tools-api.yaml                # OpenAPI contract (source of truth, committed)
    └── application.yaml

# openapi-generator output (NOT hand-edited, gitignored, on compile classpath):
build/generated/openapi/.../api/*      # generated server interfaces
build/generated/openapi/.../model/*    # generated DTO models

src/test/
├── java/com/mickels/freelancertoolsservice/
│   ├── domain/                                      # unit tests for domain model/VOs (Given/When/Then)
│   ├── application/service/                         # unit tests for use cases (ports mocked)
│   ├── infrastructure/adapter/out/persistence/      # @DataJpaTest integration tests (H2)
│   ├── infrastructure/adapter/in/web/               # web-layer slice/integration tests
│   └── functional/                                  # Cucumber runner + step definitions
└── resources/
    └── features/                                    # *.feature files (Gherkin BDD scenarios)
```

**Structure Decision**: Single-module web service using Clean Architecture package layout
(`domain`, `application`, `infrastructure`). This satisfies Principle I: the domain and
application layers contain no framework dependencies, and all framework/persistence/web concerns
live in `infrastructure` adapters that depend inward through ports. openapi-generator produces
server interfaces + DTOs under `build/generated` (never edited); web controllers in
`adapter/in/web` implement those interfaces and translate to use-case ports.

## Complexity Tracking

> No constitution violations. Section intentionally left empty.
