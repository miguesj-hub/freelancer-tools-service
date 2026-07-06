package com.mickels.freelancertoolsservice.infrastructure.config;

import com.mickels.freelancertoolsservice.application.port.in.ClassifyTimeEntryUseCase;
import com.mickels.freelancertoolsservice.application.port.in.HoursReportUseCase;
import com.mickels.freelancertoolsservice.application.port.in.LogTimeUseCase;
import com.mickels.freelancertoolsservice.application.port.in.ManageClientUseCase;
import com.mickels.freelancertoolsservice.application.port.in.ManageProjectUseCase;
import com.mickels.freelancertoolsservice.application.port.in.ManageTaskUseCase;
import com.mickels.freelancertoolsservice.application.port.out.ClientRepository;
import com.mickels.freelancertoolsservice.application.port.out.ProjectRepository;
import com.mickels.freelancertoolsservice.application.port.out.TaskRepository;
import com.mickels.freelancertoolsservice.application.port.out.TimeEntryRepository;
import com.mickels.freelancertoolsservice.application.service.ClassifyTimeEntryService;
import com.mickels.freelancertoolsservice.application.service.HoursReportService;
import com.mickels.freelancertoolsservice.application.service.LogTimeService;
import com.mickels.freelancertoolsservice.application.service.ManageClientService;
import com.mickels.freelancertoolsservice.application.service.ManageProjectService;
import com.mickels.freelancertoolsservice.application.service.ManageTaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the framework-free application services as Spring beans. Keeping the
 * annotations here (infrastructure) preserves the purity of the application layer
 * (Principle I: Clean Architecture).
 */
@Configuration
public class UseCaseConfiguration {

    @Bean
    ManageClientUseCase manageClientUseCase(ClientRepository clients, ProjectRepository projects) {
        return new ManageClientService(clients, projects);
    }

    @Bean
    ManageProjectUseCase manageProjectUseCase(ProjectRepository projects,
                                              ClientRepository clients,
                                              TaskRepository tasks) {
        return new ManageProjectService(projects, clients, tasks);
    }

    @Bean
    ManageTaskUseCase manageTaskUseCase(TaskRepository tasks,
                                        ProjectRepository projects,
                                        TimeEntryRepository timeEntries) {
        return new ManageTaskService(tasks, projects, timeEntries);
    }

    @Bean
    LogTimeUseCase logTimeUseCase(TimeEntryRepository timeEntries,
                                  TaskRepository tasks,
                                  ProjectRepository projects) {
        return new LogTimeService(timeEntries, tasks, projects);
    }

    @Bean
    ClassifyTimeEntryUseCase classifyTimeEntryUseCase(TimeEntryRepository timeEntries) {
        return new ClassifyTimeEntryService(timeEntries);
    }

    @Bean
    HoursReportUseCase hoursReportUseCase(TimeEntryRepository timeEntries) {
        return new HoursReportService(timeEntries);
    }
}
