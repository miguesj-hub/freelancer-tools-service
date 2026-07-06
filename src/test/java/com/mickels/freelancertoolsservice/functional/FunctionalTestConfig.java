package com.mickels.freelancertoolsservice.functional;

import io.cucumber.spring.ScenarioScope;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Functional-test beans. Defined here (not as component-scanned {@code @Component}s)
 * so they are only present in the Cucumber context and never pollute other
 * {@code @SpringBootTest} contexts.
 */
@TestConfiguration
public class FunctionalTestConfig {

    @Bean
    HubClient hubClient(TestRestTemplate restTemplate) {
        return new HubClient(restTemplate);
    }

    @Bean
    @ScenarioScope
    ScenarioContext scenarioContext() {
        return new ScenarioContext();
    }
}
