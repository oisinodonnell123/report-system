package com.report.system.report.service.domain.valueObject;

import java.util.List;

public class LogReport {

    private final int distinctIpsCount;
    private final List<String> topIps;
    private final List<String> topEndpoints;
    private final List<String> errorMessages;

    public LogReport(int distinctIpsCount, List<String> topIps, List<String> topEndpoints,
        List<String> errorMessages) {
        this.distinctIpsCount = distinctIpsCount;
        this.topIps = topIps;
        this.topEndpoints = topEndpoints;
        this.errorMessages = errorMessages;
    }

    public int getDistinctIpsCount() {
        return distinctIpsCount;
    }

    public List<String> getTopIps() {
        return topIps;
    }

    public List<String> getTopEndpoints() {
        return topEndpoints;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
