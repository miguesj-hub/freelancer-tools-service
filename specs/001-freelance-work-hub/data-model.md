# Phase 1 Data Model: Freelance Work Hub

Domain model expressed independently of persistence/framework (Clean Architecture). JPA `@Entity`
classes in `adapter/out/persistence` mirror these and are mapped to/from the domain model.

## Entity Relationship Overview

```text
Client 1 ──< Project 1 ──< Task 1 ──< TimeEntry
   │              │            │
   └──< Note      └──< Note    └──< Note      (Note is polymorphic-by-owner, optional)
```

Strict ownership hierarchy: a Project belongs to exactly one Client; a Task to exactly one
Project; a TimeEntry to exactly one Task. A TimeEntry's client/project are derived through its
Task (permanent, non-reassignable).

## Client

| Field | Type | Rules |
|-------|------|-------|
| id | UUID / Long (generated) | Immutable identity |
| name | String | Required, non-blank, ≤ 120 chars |
| contactEmail | String (optional) | Valid email format if present |
| notes / metadata | String (optional) | ≤ 2000 chars |
| createdAt | Timestamp | Set on creation |

- **Relationships**: owns 0..* Project.
- **Rules**: Cannot be deleted while it has ≥1 Project (FR-013).

## Project

| Field | Type | Rules |
|-------|------|-------|
| id | UUID / Long (generated) | Immutable identity |
| clientId | reference | Required, must reference an existing Client (FR-002, FR-014) |
| name | String | Required, non-blank, ≤ 120 chars |
| description | String (optional) | ≤ 2000 chars |
| createdAt | Timestamp | Set on creation |

- **Relationships**: belongs to 1 Client; owns 0..* Task.
- **Rules**: Cannot be deleted while it has ≥1 Task (FR-013). Creating with a non-existent
  clientId is rejected (no orphan — FR-014).

## Task

| Field | Type | Rules |
|-------|------|-------|
| id | UUID / Long (generated) | Immutable identity |
| projectId | reference | Required, must reference an existing Project (FR-003, FR-014) |
| title | String | Required, non-blank, ≤ 200 chars |
| description | String (optional) | ≤ 2000 chars |
| status | TaskStatus enum | Required; defaults to `TO_DO` on creation (FR-006) |
| createdAt | Timestamp | Set on creation |

- **Relationships**: belongs to 1 Project (transitively to 1 Client); owns 0..* TimeEntry.
- **Rules**: Cannot be deleted while it has ≥1 TimeEntry (FR-013). Creating with a non-existent
  projectId is rejected (FR-014).

### TaskStatus (enum / value object)

`TO_DO | IN_PROGRESS | DONE`

- New tasks start at `TO_DO` (FR-006).
- Transitions allowed among all three states (FR-007); any other value rejected (FR-005, FR-014).

```text
      ┌──────────────────────────────┐
      ▼                              │
   TO_DO  ⇄  IN_PROGRESS  ⇄  DONE ───┘   (all pairwise transitions permitted)
```

## TimeEntry

| Field | Type | Rules |
|-------|------|-------|
| id | UUID / Long (generated) | Immutable identity |
| taskId | reference | Required, must reference an existing Task (FR-008, FR-014); immutable after creation (FR-009) |
| minutes | Integer (duration) | Required, **> 0** (FR-014); rejects zero/negative |
| workDate | Date/DateTime | Required; when the work occurred |
| type | TimeEntryType enum | Required; defaults to `BILLABLE` when unspecified (FR-011, Assumptions) |
| description | String (optional) | ≤ 2000 chars |
| createdAt | Timestamp | Set on creation |

- **Relationships**: belongs to 1 Task; derives project + client through the Task (permanent).
- **Rules**: task/project/client association is set at creation and never changes (FR-009). A task
  may hold TimeEntries of both types simultaneously (FR-011).

### TimeEntryType (enum / value object)

`BILLABLE | ADMINISTRATIVE` — classification lives on the TimeEntry, not the Task.

## Note (optional — FR-016)

| Field | Type | Rules |
|-------|------|-------|
| id | UUID / Long (generated) | Immutable identity |
| ownerType | enum {CLIENT, PROJECT, TASK} | Required |
| ownerId | reference | Required, must reference an existing owner of ownerType |
| body | String | Required, non-blank, ≤ 4000 chars |
| createdAt | Timestamp | Set on creation |

- Delivered after US1–US3 if effort allows; does not block MVP.

## Derived / Reporting Model — HoursReport (FR-012)

Not persisted; computed on request. Aggregates TimeEntry durations grouped by `TimeEntryType`,
scoped by an optional filter (clientId, projectId, or taskId).

| Field | Type | Meaning |
|-------|------|---------|
| scope | {ALL, CLIENT, PROJECT, TASK} + id | What the report covers |
| billableMinutes | Integer | Sum of BILLABLE entry durations in scope |
| administrativeMinutes | Integer | Sum of ADMINISTRATIVE entry durations in scope |
| totalMinutes | Integer | billable + administrative |

## Validation Summary (traceability)

| Rule | Source |
|------|--------|
| Project requires existing Client; Task requires existing Project; TimeEntry requires existing Task | FR-002, FR-003, FR-008, FR-014 |
| Task defaults to TO_DO; only 3 states valid | FR-005, FR-006, FR-007 |
| TimeEntry duration > 0 | FR-014 |
| TimeEntry type on entry, default BILLABLE, both types per task | FR-011, Assumptions |
| Permanent client/project/task association on TimeEntry | FR-009 |
| No delete while dependents exist | FR-013 |
| Hours report grouped by type, scopable | FR-012 |
