package com.report.system.report.service.domain.dto;

public class LogEntry {
    private final String ip;
    private final String endpoint;

    public LogEntry(String ip, String endpoint) {
        this.ip = ip;
        this.endpoint = endpoint;
    }

    public String getIp() {
        return ip;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
