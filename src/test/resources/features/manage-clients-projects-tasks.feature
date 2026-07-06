Feature: Manage clients, projects and tasks in one hub
  As a freelancer I manage clients, projects and tasks in a single place (US1).

  Scenario: Register a client, a project and a task
    Given a client "Acme Studio"
    And a project "Website Redesign" for that client
    And a task "Design homepage" for that project
    Then the task is stored with status "TO_DO"

  Scenario: Move a task through its states
    Given a client "Acme Studio"
    And a project "Website Redesign" for that client
    And a task "Design homepage" for that project
    When I set the task status to "IN_PROGRESS"
    Then the task is stored with status "IN_PROGRESS"
    When I set the task status to "DONE"
    Then the task is stored with status "DONE"

  Scenario: Deleting a client that still has projects is rejected
    Given a client "Acme Studio"
    And a project "Website Redesign" for that client
    When I delete the client
    Then the request is rejected with status 409
