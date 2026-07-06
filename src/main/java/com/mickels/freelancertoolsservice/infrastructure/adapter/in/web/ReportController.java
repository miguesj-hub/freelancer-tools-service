package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.api.ReportsApi;
import com.mickels.freelancertoolsservice.api.model.HoursReport;
import com.mickels.freelancertoolsservice.application.port.in.HoursReportUseCase;
import com.mickels.freelancertoolsservice.infrastructure.adapter.in.web.mapper.WebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController implements ReportsApi {

    private final HoursReportUseCase useCase;

    public ReportController(HoursReportUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<HoursReport> getHoursReport(String clientId, String projectId, String taskId) {
        var report = useCase.report(
                WebMapper.parseId(clientId),
                WebMapper.parseId(projectId),
                WebMapper.parseId(taskId));
        return ResponseEntity.ok(WebMapper.toDto(report));
    }
}
