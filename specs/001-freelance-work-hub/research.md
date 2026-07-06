# Phase 0 Research: Freelance Work Hub

All Technical Context items are determined by the project constitution and `build.gradle`, so
there were no open `NEEDS CLARIFICATION` items. This document records the key technology and
approach decisions and their rationale.

## 1. Architecture layout for Clean Architecture on Spring Boot

- **Decision**: Package-by-layer within a single Gradle module: `domain`, `application`
  (`port/in`, `port/out`, `service`), `infrastructure` (`adapter/in/web`,
  `adapter/out/persistence`, `config`). Dependencies point inward only.
- **Rationale**: Keeps enterprise + application rules framework-free (Principle I) while staying
  a simple single deployable (YAGNI — no multi-module Gradle overhead for an MVP). Ports/adapters
  make persistence and web swappable and independently testable.
- **Alternatives considered**: Multi-module Gradle (one module per layer) — rejected as
  over-engineering for a single-service MVP; classic Spring layered (controller/service/repo with
  JPA entities as the domain) — rejected because it couples domain to JPA, violating Principle I.

## 2. Domain purity vs. Lombok & JPA

- **Decision**: Domain model classes are plain Java (hand-written constructors/factory methods
  and behavior). Separate JPA `@Entity` classes live in `adapter/out/persistence` and are mapped
  to/from domain objects. Lombok is used freely in outer layers (DTOs, JPA entities, config) and
  sparingly in domain (e.g., `@Getter`/`@Value` only), never persistence annotations in domain.
- **Rationale**: Satisfies the constitution's requirement that Lombok/JPA must not leak into the
  inner layers. Explicit mapping isolates persistence detail.
- **Alternatives considered**: Using JPA entities directly as the domain model — rejected
  (framework leak, breaks Dependency Rule).

## 3. API First tooling (OpenAPI + openapi-generator)

- **Decision**: Author `freelancer-tools-api.yaml` (OpenAPI 3.0) as the single source of truth,
  committed under `src/main/resources/openapi/` (and mirrored to the feature `contracts/`). Use
  the `org.openapi.generator` Gradle plugin with `generatorName = "spring"`,
  `interfaceOnly = true`, `useSpringBoot3 = true` (Jakarta), generating API interfaces + model
  DTOs into `build/generated`. Controllers implement the generated `*Api` interfaces.
- **Rationale**: `interfaceOnly` keeps controllers hand-written (delegating to use cases) while
  the contract drives signatures and DTOs; generated code is regenerated, never edited
  (Principle IV). Adding the generated dir to the source set puts DTOs on the compile classpath.
- **Alternatives considered**: springdoc/annotation-first (code-first) — rejected, violates API
  First. Full `generateApis`+delegate pattern — heavier than needed for the MVP.

## 4. BDD test strategy across the three levels

- **Decision**:
  - **Unit** (domain + use-case services): JUnit 5 + Mockito + AssertJ, method names and
    structure expressing Given/When/Then; ports mocked.
  - **Integration** (persistence adapters): `@DataJpaTest` against H2, verifying Spring Data
    repositories + persistence adapters + mappers; Given/When/Then structure.
  - **Functional** (end-to-end over the API): Cucumber-JVM (Gherkin `.feature` files) driving
    `@SpringBootTest` (random port) via a REST client, mapping directly to the spec's acceptance
    scenarios.
- **Rationale**: Cucumber gives literal Given/When/Then business-readable functional specs that
  trace 1:1 to acceptance scenarios; JUnit5 Given/When/Then keeps fast, focused unit/integration
  tests. All three levels required by Principle II.
- **Alternatives considered**: JBehave (less common, heavier) — rejected; only JUnit for
  everything — rejected because functional scenarios lose business-readable Gherkin traceability.

## 5. Coverage enforcement (JaCoCo gates)

- **Decision**: Apply the `jacoco` Gradle plugin; `jacocoTestReport` produces HTML/XML;
  `jacocoTestCoverageVerification` enforces two rules — a **BUNDLE**-level (global) rule at
  ≥ 0.80 and a **CLASS**-level rule at > 0.80 (minimum expressed as ≥ 0.8001 / strictly greater).
  `check` depends on the verification task so the build fails on breach. Exclude generated
  OpenAPI classes, the Spring Boot `*Application` bootstrap, and generated Lombok/config
  boilerplate from counting.
- **Rationale**: Directly encodes Principle V as a build gate. Excluding generated code keeps the
  metric meaningful (we don't test framework-generated stubs).
- **Alternatives considered**: Global-only threshold — rejected, constitution requires per-class
  too. Coverage as advisory (report-only) — rejected, must fail the build.

## 6. Task state transitions

- **Decision**: `TaskStatus` enum {TO_DO, IN_PROGRESS, DONE}; new tasks default to TO_DO;
  transitions among the three are freely allowed (no restricted state machine in the MVP). Unknown
  values rejected at the boundary and by the enum.
- **Rationale**: Matches spec FR-005..FR-007 and the "agile standard, MVP simplicity" note; a
  restrictive transition graph would be YAGNI for this story.
- **Alternatives considered**: Enforced linear flow To Do→In Progress→Done — rejected (spec allows
  moving among states; freelancers reopen work).

## 7. Time-entry classification & permanent association

- **Decision**: `TimeEntryType` enum {BILLABLE, ADMINISTRATIVE}; classification stored on the time
  entry (not the task); default BILLABLE when unspecified. Each time entry stores immutable
  references to task (and, through the task, project + client); association is not reassignable
  after creation.
- **Rationale**: FR-009, FR-011 and the implementation note that the same task may have hours of
  both types. Reporting groups by type (FR-012).
- **Alternatives considered**: Classification on the task — rejected explicitly by the story.

## 8. Deletion / historical-data protection

- **Decision**: Restrict deletion of a client/project/task while dependent records (projects,
  tasks, or time entries) exist — return a domain error rather than cascade-deleting time history.
- **Rationale**: FR-013 requires no silent loss of historical time data.
- **Alternatives considered**: Soft-delete flags — reasonable but heavier than needed for MVP;
  cascade delete — rejected (loses history).

## 9. Entity identity (ID type)

- **Decision**: All entities use **UUID** identifiers, generated by the persistence layer
  (`@GeneratedValue(strategy = UUID)`), stored as `UUID` columns in H2, and serialized as
  `string` in the API contract (matches OpenAPI `type: string`). Domain models carry the id as
  `UUID`; DTO mappers convert to/from string. No database sequences.
- **Rationale**: Matches the contract's `string` IDs directly, avoids exposing sequential record
  counts, and needs no DB sequence — resolving the previously deferred UUID-vs-Long choice.
- **Alternatives considered**: Auto-increment `Long` — rejected (leaks record counts, requires a
  sequence, and would force numeric path params diverging from the `string` contract).
