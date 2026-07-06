package com.mickels.freelancertoolsservice.infrastructure.adapter.in.web;

import com.mickels.freelancertoolsservice.api.TimeEntriesApi;
import com.mickels.freelancertoolsservice.api.model.TimeEntry;
import com.mickels.freelancertoolsservice.api.model.TimeEntryClassificationUpdate;
import com.mickels.freelancertoolsservice.api.model.TimeEntryRequest;
import com.mickels.freelancertoolsservice.application.port.in.ClassifyTimeEntryUseCase;
import com.mickels.freelancertoolsservice.application.port.in.LogTimeUseCase;
import com.mickels.freelancertoolsservice.application.port.in.LogTimeUseCase.LogTimeCommand;
import com.mickels.freelancertoolsservice.infrastructure.adapter.in.web.mapper.WebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TimeEntryController implements TimeEntriesApi {

    private final LogTimeUseCase logTimeUseCase;
    private final ClassifyTimeEntryUseCase classifyUseCase;

    public TimeEntryController(LogTimeUseCase logTimeUseCase, ClassifyTimeEntryUseCase classifyUseCase) {
        this.logTimeUseCase = logTimeUseCase;
        this.classifyUseCase = classifyUseCase;
    }

    @Override
    public ResponseEntity<TimeEntry> logTimeEntry(String taskId, TimeEntryRequest request) {
        var created = logTimeUseCase.log(WebMapper.parseId(taskId),
                new LogTimeCommand(request.getMinutes(), request.getWorkDate(),
                        WebMapper.toDomain(request.getType()), request.getDescription()));
        return ResponseEntity.status(HttpStatus.CREATED).body(WebMapper.toDto(created));
    }

    @Override
    public ResponseEntity<List<TimeEntry>> listTimeEntriesByTask(String taskId) {
        return ResponseEntity.ok(logTimeUseCase.listByTask(WebMapper.parseId(taskId))
                .stream().map(WebMapper::toDto).toList());
    }

    @Override
    public ResponseEntity<TimeEntry> classifyTimeEntry(String timeEntryId, TimeEntryClassificationUpdate request) {
        var updated = classifyUseCase.classify(WebMapper.parseId(timeEntryId),
                WebMapper.toDomain(request.getType()));
        return ResponseEntity.ok(WebMapper.toDto(updated));
    }
}
