package com.mickels.freelancertoolsservice.domain.vo;

/**
 * Time-entry classification (FR-011). Lives on the time entry, not the task, so
 * a single task may accrue hours of both types. Defaults to {@link #BILLABLE}.
 */
public enum TimeEntryType {
    BILLABLE,
    ADMINISTRATIVE
}
