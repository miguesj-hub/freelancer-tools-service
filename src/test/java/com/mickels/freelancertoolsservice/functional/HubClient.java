package com.mickels.freelancertoolsservice.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/** Thin HTTP helper over TestRestTemplate for functional steps. */
public class HubClient {

    // TestRestTemplate's rootUri already includes the server context-path (/api/v1).
    private static final String BASE = "";
    private final TestRestTemplate rest;
    private final ObjectMapper mapper = new ObjectMapper();

    public HubClient(TestRestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<String> post(String path, Object body) {
        return rest.postForEntity(BASE + path, body, String.class);
    }

    public ResponseEntity<String> get(String path) {
        return rest.getForEntity(BASE + path, String.class);
    }

    public ResponseEntity<String> patch(String path, Object body) {
        return rest.exchange(BASE + path, HttpMethod.PATCH, new HttpEntity<>(body), String.class);
    }

    public ResponseEntity<String> delete(String path) {
        return rest.exchange(BASE + path, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
    }

    /** Extracts a top-level string field from a JSON response body. */
    public String field(ResponseEntity<String> response, String name) {
        try {
            return mapper.readTree(response.getBody()).get(name).asText();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read '" + name + "' from: " + response.getBody(), e);
        }
    }

    public int arraySize(ResponseEntity<String> response) {
        try {
            return mapper.readTree(response.getBody()).size();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read array from: " + response.getBody(), e);
        }
    }

    public long number(ResponseEntity<String> response, String name) {
        try {
            return mapper.readTree(response.getBody()).get(name).asLong();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read '" + name + "' from: " + response.getBody(), e);
        }
    }
}
