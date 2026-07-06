package com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.mapper;

import com.mickels.freelancertoolsservice.domain.model.Client;
import com.mickels.freelancertoolsservice.domain.model.Project;
import com.mickels.freelancertoolsservice.domain.model.Task;
import com.mickels.freelancertoolsservice.domain.model.TimeEntry;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.ClientJpaEntity;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.ProjectJpaEntity;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.TaskJpaEntity;
import com.mickels.freelancertoolsservice.infrastructure.adapter.out.persistence.TimeEntryJpaEntity;

/** Maps between JPA entities (infrastructure) and domain models. */
public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    // ---- Client ----

    public static ClientJpaEntity toEntity(Client client) {
        ClientJpaEntity e = new ClientJpaEntity();
        if (client.getId() != null) {
            e.setId(client.getId());
        }
        if (client.getCreatedAt() != null) {
            e.setCreatedAt(client.getCreatedAt());
        }
        e.setName(client.getName());
        e.setContactEmail(client.getContactEmail());
        e.setNotes(client.getNotes());
        return e;
    }

    public static Client toDomain(ClientJpaEntity e) {
        return new Client(e.getId(), e.getName(), e.getContactEmail(), e.getNotes(), e.getCreatedAt());
    }

    // ---- Project ----

    public static ProjectJpaEntity toEntity(Project project) {
        ProjectJpaEntity e = new ProjectJpaEntity();
        if (project.getId() != null) {
            e.setId(project.getId());
        }
        if (project.getCreatedAt() != null) {
            e.setCreatedAt(project.getCreatedAt());
        }
        e.setClientId(project.getClientId());
        e.setName(project.getName());
        e.setDescription(project.getDescription());
        e.setNotes(project.getNotes());
        return e;
    }

    public static Project toDomain(ProjectJpaEntity e) {
        return new Project(e.getId(), e.getClientId(), e.getName(), e.getDescription(), e.getNotes(), e.getCreatedAt());
    }

    // ---- Task ----

    public static TaskJpaEntity toEntity(Task task) {
        TaskJpaEntity e = new TaskJpaEntity();
        if (task.getId() != null) {
            e.setId(task.getId());
        }
        if (task.getCreatedAt() != null) {
            e.setCreatedAt(task.getCreatedAt());
        }
        e.setProjectId(task.getProjectId());
        e.setTitle(task.getTitle());
        e.setDescription(task.getDescription());
        e.setNotes(task.getNotes());
        e.setStatus(task.getStatus());
        return e;
    }

    public static Task toDomain(TaskJpaEntity e) {
        return new Task(e.getId(), e.getProjectId(), e.getTitle(), e.getDescription(),
                e.getNotes(), e.getStatus(), e.getCreatedAt());
    }

    // ---- TimeEntry ----

    public static TimeEntryJpaEntity toEntity(TimeEntry entry) {
        TimeEntryJpaEntity e = new TimeEntryJpaEntity();
        if (entry.getId() != null) {
            e.setId(entry.getId());
        }
        if (entry.getCreatedAt() != null) {
            e.setCreatedAt(entry.getCreatedAt());
        }
        e.setTaskId(entry.getTaskId());
        e.setProjectId(entry.getProjectId());
        e.setClientId(entry.getClientId());
        e.setMinutes(entry.getMinutes());
        e.setWorkDate(entry.getWorkDate());
        e.setType(entry.getType());
        e.setDescription(entry.getDescription());
        return e;
    }

    public static TimeEntry toDomain(TimeEntryJpaEntity e) {
        return new TimeEntry(e.getId(), e.getTaskId(), e.getProjectId(), e.getClientId(),
                e.getMinutes(), e.getWorkDate(), e.getType(), e.getDescription(), e.getCreatedAt());
    }
}
