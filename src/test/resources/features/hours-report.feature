Feature: Classify time and report hours
  Hours are grouped by billable vs administrative (US3).

  Background:
    Given a client "Acme Studio"
    And a project "Website Redesign" for that client
    And a task "Design homepage" for that project

  Scenario: Report separates billable and administrative hours
    When I log 90 minutes on the task as "BILLABLE"
    And I log 30 minutes on the task as "ADMINISTRATIVE"
    Then the hours report for the client shows 90 billable and 30 administrative

  Scenario: Re-classify a time entry and see it move in the report
    When I log 30 minutes on the task as "BILLABLE"
    And I reclassify the last time entry as "ADMINISTRATIVE"
    Then the hours report for the client shows 0 billable and 30 administrative
