Feature: Log time against tasks
  Time entries are permanently tied to task, project and client (US2).

  Background:
    Given a client "Acme Studio"
    And a project "Website Redesign" for that client
    And a task "Design homepage" for that project

  Scenario: Log time and keep associations after a status change
    When I log 90 minutes on the task
    Then the time entry is associated with the task, project and client
    When I set the task status to "DONE"
    Then the time entry still lists under the task

  Scenario: Reject a non-positive duration
    When I log 0 minutes on the task
    Then the request is rejected with status 400

  Scenario: Multiple entries on one task all persist and list
    When I log 30 minutes on the task
    And I log 15 minutes on the task
    Then the task has 2 time entries

  Scenario: Logging against a non-existent task is rejected
    When I log 60 minutes on a random task
    Then the request is rejected with status 404
