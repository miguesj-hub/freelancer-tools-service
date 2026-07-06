package com.mickels.freelancertoolsservice.domain.vo;

/** The three allowed task states (FR-005). New tasks default to {@link #TO_DO} (FR-006). */
public enum TaskStatus {
    TO_DO,
    IN_PROGRESS,
    DONE
}
