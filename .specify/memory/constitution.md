<!--
Sync Impact Report
==================
Version change: (none / template) → 1.0.0
Bump rationale: Initial ratification of the project constitution (MAJOR baseline).

Modified principles: N/A (first adoption)
Added principles:
  - I. Clean Architecture (Robert C. Martin)
  - II. BDD Testing Discipline (Unit, Integration, Functional)
  - III. SOLID, YAGNI & DRY
  - IV. API First (OpenAPI Contract + openapi-generator)
  - V. Coverage Gates (JaCoCo: per-class > 80%, global >= 80%)
Added sections:
  - Technology Stack & Constraints
  - Development Workflow & Quality Gates

Removed sections: None

Templates requiring updates:
  ✅ .specify/templates/plan-template.md  — Constitution Check aligns (gates reference principles below)
  ✅ .specify/templates/spec-template.md  — no structural change required
  ✅ .specify/templates/tasks-template.md — testing/contract task types already supported
  ✅ .specify/templates/checklist-template.md — no change required

Follow-up TODOs: None
-->

# Freelancer Tools Service Constitution

## Core Principles

### I. Clean Architecture (Robert C. Martin)

The system MUST follow Clean Architecture as defined by Robert C. Martin. Code is
organized in concentric layers with dependencies pointing **inward only**:

- **Domain / Entities**: enterprise business rules. MUST be pure Java with no
  dependency on Spring, JPA, Lombok annotations that couple to frameworks, or any
  outer layer.
- **Use Cases / Application**: application-specific business rules. Orchestrate
  entities and define input/output ports (interfaces). MUST NOT depend on
  frameworks, controllers, or persistence details.
- **Interface Adapters**: controllers, presenters, gateways, and Spring Data JPA
  repository implementations that adapt outer detail to inner ports.
- **Frameworks & Drivers**: Spring Boot, H2, web layer, configuration.

The Dependency Rule is NON-NEGOTIABLE: source-code dependencies point only toward
higher-level policy. Inner layers MUST NOT know about outer layers. Crossing
boundaries happens through interfaces (ports) and dependency inversion.

**Rationale**: Isolating business rules from delivery and infrastructure keeps the
core independently testable, replaceable, and resistant to framework churn.

### II. BDD Testing Discipline (Unit, Integration, Functional)

All behavior MUST be covered by tests written in Behavior-Driven Development (BDD)
style using the **Given–When–Then** structure. Three test levels are mandatory:

- **Unit tests**: verify a single use case, entity, or adapter in isolation
  (collaborators mocked). Cover domain and application logic.
- **Integration tests**: verify adapters against real infrastructure (Spring
  Data JPA repositories against H2, wiring, transactions).
- **Functional tests**: verify end-to-end behavior against the generated API
  contract, exercising complete user scenarios.

Every functional requirement and acceptance scenario MUST map to at least one BDD
scenario. Test names and structure MUST make the Given/When/Then intent explicit.

**Rationale**: BDD ties tests to observable behavior and to the spec's acceptance
scenarios, keeping tests meaningful and traceable rather than implementation-coupled.

### III. SOLID, YAGNI & DRY

Production code MUST adhere to the following engineering practices:

- **SOLID**: single responsibility per class; open for extension / closed for
  modification; substitutable abstractions; segregated, client-specific interfaces;
  dependency inversion via ports (reinforcing Principle I).
- **YAGNI**: implement only what a current, specified requirement demands. No
  speculative abstraction, configuration, or extension points "for later".
- **DRY**: no duplicated business logic. Shared behavior is extracted into a single
  authoritative location — but not at the cost of introducing accidental coupling
  (prefer duplication over the wrong abstraction until the shape is proven).

**Rationale**: These practices keep the codebase changeable and comprehensible;
they are the day-to-day mechanics that make Clean Architecture sustainable.

### IV. API First (OpenAPI Contract + openapi-generator)

APIs MUST be designed contract-first. Before implementation:

- An **OpenAPI contract** (the single source of truth for the API) MUST exist and
  be committed to version control.
- Server interfaces, DTOs, and models MUST be produced with **openapi-generator**
  from that contract. Generated code MUST NOT be hand-edited; regenerate instead.
- Controllers implement the generated interfaces; they adapt the contract to
  application use cases (per Principle I).
- Any API change starts as a contract change, reviewed before code changes.

**Rationale**: A contract-first flow guarantees the API is intentional, documented,
consumer-verifiable, and consistent between spec and implementation.

### V. Coverage Gates (JaCoCo: per-class > 80%, global >= 80%)

Test coverage MUST be measured with **JaCoCo** and enforced as a build gate:

- **Per-class coverage MUST be strictly greater than 80%.**
- **Global (overall) coverage MUST be greater than or equal to 80%.**
- The JaCoCo report is generated on every build, and the coverage verification
  task MUST fail the build when a threshold is not met.
- Generated code (e.g., openapi-generator output) and pure boilerplate MAY be
  excluded from coverage counting, but such exclusions MUST be explicit and
  justified in the build configuration.

**Rationale**: Enforced coverage thresholds make the BDD discipline verifiable and
prevent untested code from reaching the main branch.

## Technology Stack & Constraints

- **Language / Runtime**: Java 21 (toolchain-pinned).
- **Framework**: Spring Boot 4.1.x.
- **Persistence**: Spring Data JPA with an H2 database.
- **Boilerplate reduction**: Lombok — permitted in outer layers (adapters, DTOs,
  configuration). Lombok MUST NOT leak framework/persistence concerns into domain
  entities in a way that violates Principle I's purity of the inner layers.
- **Build tool**: Gradle.
- **API tooling**: OpenAPI contracts + openapi-generator (per Principle IV).
- **Coverage tooling**: JaCoCo (per Principle V).

Introducing a new framework, database, or cross-cutting dependency MUST be justified
against YAGNI and recorded in the plan's Complexity Tracking section.

## Development Workflow & Quality Gates

- **Contract before code**: no endpoint implementation is merged without its
  OpenAPI contract change reviewed and merged (or co-reviewed) first.
- **Tests before merge**: unit, integration, and functional BDD tests for the
  changed behavior MUST accompany the change.
- **Coverage gate**: the build MUST run JaCoCo verification; a failing threshold
  blocks merge.
- **Layer boundary check**: reviewers MUST verify the Dependency Rule is not
  violated (no inward dependency on outer layers, no framework leakage into domain).
- **Green build required**: all tests and quality gates MUST pass before merge to
  the main branch.

## Governance

This constitution supersedes other development practices for this repository. When
guidance conflicts, this document wins.

- **Amendments**: proposed via pull request that edits this file, states the
  rationale, and updates the version and Sync Impact Report. Amendments require
  review and approval before merge.
- **Versioning policy** (semantic):
  - **MAJOR**: backward-incompatible governance changes or removal/redefinition of
    a principle.
  - **MINOR**: a new principle or section, or materially expanded guidance.
  - **PATCH**: clarifications, wording, and non-semantic refinements.
- **Compliance review**: every PR and code review MUST verify compliance with the
  principles above. Deviations MUST be justified in the plan's Complexity Tracking
  table or the PR description; unjustified violations block merge.

**Version**: 1.0.0 | **Ratified**: 2026-07-05 | **Last Amended**: 2026-07-05
