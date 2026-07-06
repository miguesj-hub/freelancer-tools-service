# Feature Specification: Freelance Work Hub — Clients, Projects, Tasks & Time

**Feature Branch**: `001-freelance-work-hub`

**Created**: 2026-07-05

**Status**: Draft

**Input**: User description: "US-01 · Gestionar clientes, proyectos, tareas y tiempo en un solo lugar (épica E-01). Como freelancer independiente quiero gestionar mis clientes, proyectos, tareas, tiempo y notas en un solo lugar, para no necesitar sincronizar manualmente entre Trello, Toggl, Sheets y Notion."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Register clients, projects and tasks in one place (Priority: P1)

As an independent freelancer, I want to create a client, add projects to that client, and
create tasks under those projects, so that all of my work is organized in a single hub
without touching any external tool.

**Why this priority**: This is the foundational hub. Without the ability to record clients,
projects and tasks, no time can be logged and no reporting is possible. It is the minimum
viable slice that already delivers value: a single place to see all engagements.

**Independent Test**: Create a client with no prior tasks, add a project to it, then create a
task under that project. Verify the task is stored and retrievable, tied to its project and
client, with no step performed in any other tool.

**Acceptance Scenarios**:

1. **Given** a client with no tasks yet, **When** I create a task associated with that client
   (through one of its projects), **Then** the task is recorded and retrievable without any
   action in another tool.
2. **Given** an existing client, **When** I create a new project for that client, **Then** the
   project is stored and linked to the client.
3. **Given** a newly created task, **When** I view it, **Then** its state defaults to "To Do".
4. **Given** a task in any state, **When** I change its state to "In Progress" or "Done",
   **Then** the new state is persisted and reflected on retrieval.

---

### User Story 2 - Log time against tasks with permanent client/project/task association (Priority: P2)

As a freelancer, I want to log time entries against a specific task, so that each recorded
duration is permanently associated with the correct client, project and task and never has to
be reconciled manually.

**Why this priority**: Time tracking is the core value driver (billing, reporting), but it
depends on clients, projects and tasks existing first (P1).

**Independent Test**: With an existing task in "To Do", record a time entry against it and
verify the entry is stored and permanently linked to that task, its project and its client.

**Acceptance Scenarios**:

1. **Given** a task in state "To Do", **When** I log time against that task, **Then** the time
   entry is permanently associated with the task, its project and its client.
2. **Given** a logged time entry, **When** the associated task later changes state, **Then** the
   time entry remains associated with the same client, project and task.
3. **Given** a task, **When** I log multiple time entries against it, **Then** each entry is
   stored independently and all are retrievable for that task.

---

### User Story 3 - Classify time as billable or administrative for hour reports (Priority: P3)

As a freelancer, I want to mark each time entry as "billable" or "administrative", so that my
hour reports separate revenue-generating work from overhead.

**Why this priority**: Classification adds reporting value on top of raw time tracking (P2). It
is meaningful only once time entries exist.

**Independent Test**: Mark a recorded time entry as "billable" and another as "administrative",
then request an hours report and verify each entry is grouped under its type.

**Acceptance Scenarios**:

1. **Given** a recorded time entry, **When** I mark it as "billable" or "administrative", **Then**
   the classification is persisted on that entry.
2. **Given** time entries of both types under the same task, **When** I view an hours report,
   **Then** billable and administrative hours are reported separately.
3. **Given** the same task, **When** it has both billable and administrative entries, **Then** the
   task correctly reflects hours of both types (classification lives on the entry, not the task).

---

### Edge Cases

- What happens when a user tries to create a task for a project that does not exist, or a
  project for a client that does not exist? The system MUST reject the operation and not create
  an orphan record.
- What happens when a user tries to delete a client or project that still has tasks or time
  entries? The system MUST prevent silent loss of historical time data (see FR-013).
- How does the system handle a time entry with zero or negative duration? It MUST reject
  invalid durations.
- How does the system handle an invalid task-state transition or an unknown state value? Only
  the states To Do, In Progress and Done are accepted.
- What happens if a time entry is created without a billable/administrative classification? See
  Assumptions — a default classification applies.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow a user to create, read, update and list **clients**.
- **FR-002**: System MUST allow a user to create **projects** and associate each project with
  exactly one existing client.
- **FR-003**: System MUST allow a user to create **tasks** and associate each task with exactly
  one existing project (and, transitively, that project's client).
- **FR-004**: System MUST record a task's creation even when its client had no prior tasks, with
  no dependency on any external tool.
- **FR-005**: System MUST support exactly three task states — **To Do, In Progress, Done** — and
  reject any other value.
- **FR-006**: System MUST default a newly created task to the **To Do** state.
- **FR-007**: Users MUST be able to change a task's state among the three allowed states, and the
  change MUST be persisted.
- **FR-008**: System MUST allow a user to log **time entries** against an existing task, each with
  a duration and the date/time the work occurred.
- **FR-009**: System MUST permanently associate every time entry with its task, that task's
  project, and that project's client; this association MUST NOT change afterward.
- **FR-010**: System MUST allow a task to hold multiple independent time entries.
- **FR-011**: System MUST allow each time entry to be classified as **billable** or
  **administrative**, with the classification stored on the time entry (not the task).
- **FR-012**: System MUST provide an **hours report** that groups and totals time by
  classification (billable vs. administrative), and that can be scoped to a client, project or
  task.
- **FR-013**: System MUST prevent the loss of historical time data when deleting a client,
  project or task (e.g., block deletion while dependent records exist, or otherwise preserve the
  time history).
- **FR-014**: System MUST reject invalid inputs, including orphan references (non-existent client/
  project/task) and non-positive time-entry durations.
- **FR-015**: System operates as an **independent hub** with **no synchronization** to third-party
  tools (Trello, Toggl, Notion, Sheets). Historical data from other tools is migrated manually or
  abandoned by the user.
- **FR-016**: System SHOULD allow a user to attach **notes** to a client, project or task, since
  centralizing notes is part of the single-place goal. *(Lower priority than P1–P3 above; see
  Assumptions for scope.)*

### Key Entities *(include if feature involves data)*

- **Client**: An entity the freelancer works for. Owns zero or more projects. Attributes: name/
  identifier, contact/descriptive metadata.
- **Project**: A body of work belonging to exactly one client. Owns zero or more tasks.
  Attributes: name, owning client, descriptive metadata.
- **Task**: A unit of work belonging to exactly one project. Has a state (To Do / In Progress /
  Done). Owns zero or more time entries. Attributes: title, description, state, owning project.
- **Time Entry**: A recorded duration of work against exactly one task. Permanently linked to the
  task, its project and its client. Classified as billable or administrative. Attributes:
  duration, date/time of work, classification.
- **Note** *(optional)*: Free-form text attached to a client, project or task to centralize
  contextual information.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A freelancer can create a client, a project and a first task entirely within the
  hub, performing zero steps in any external tool.
- **SC-002**: 100% of logged time entries remain correctly attributed to their original client,
  project and task after subsequent changes (e.g., task state changes).
- **SC-003**: A freelancer can log time against a task in under 30 seconds from an existing task.
- **SC-004**: An hours report separates billable from administrative time with 100% of entries
  classified under the correct type.
- **SC-005**: A freelancer can produce a full picture of a client's work (projects, tasks, and
  hours by type) without consulting Trello, Toggl, Sheets or Notion.

## Assumptions

- **Hierarchy**: The data model is a strict hierarchy Client → Project → Task → Time Entry. Every
  task belongs to a project and every project belongs to a client; a task therefore has a client
  transitively. "A task associated with a client" is satisfied through the task's project. This
  is the simplest interpretation consistent with time entries tying to client, project and task.
- **Single user context**: The MVP serves a single freelancer (the owner of the data); multi-user
  accounts, sharing, and permissions are out of scope for this story.
- **Task states**: Limited to To Do, In Progress, Done (agile standard, MVP simplicity). No
  custom or additional states in this story.
- **Time-entry classification default**: If a time entry is logged without an explicit
  classification, it defaults to **billable** (the common case for freelancers); the user can
  re-classify it.
- **Notes scope**: Notes (FR-016) are a supporting capability of the hub. If effort is
  constrained, notes may be delivered after US1–US3 without blocking the MVP.
- **No third-party integration**: Per R-18, there is no import/export sync with external tools in
  this feature; manual data entry is assumed.
- **Time input**: Time is entered manually (duration and work date); live start/stop timers are
  not required for this story.
