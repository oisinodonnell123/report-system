package com.report.system.report.service.domain;

import com.report.system.report.service.domain.dto.LogEntry;
import com.report.system.report.service.domain.exception.ParsingException;
import com.report.system.report.service.domain.parser.LogParser;
import com.report.system.report.service.domain.service.ReportDomainService;
import com.report.system.report.service.domain.valueObject.LogReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportDomainServiceImpl implements ReportDomainService {

    @Override
    public LogReport processLogEntries(List<String> logLines) {
        Set<String> distinctIPs = new HashSet<>();
        Map<String, Long> ipFrequencyMap = new HashMap<>();
        Map<String, Long> endpointFrequencyMap = new HashMap<>();
        List<String> failureMessages = new ArrayList<>();

        logLines.forEach(logLine -> {
            try {
                LogEntry logEntry = LogParser.parseLogEntry(logLine);
                distinctIPs.add(logEntry.getIp());
                ipFrequencyMap.merge(logEntry.getIp(), 1L, Long::sum);
                endpointFrequencyMap.merge(logEntry.getEndpoint(), 1L, Long::sum);
            } catch (ParsingException e) {
                failureMessages.add(e.getMessage());
            }
        });

        List<String> topThreeIPs = getTopEntries(ipFrequencyMap, 3);
        List<String> topThreeEndpoints = getTopEntries(endpointFrequencyMap, 3);

        return new LogReport(distinctIPs.size(), topThreeIPs, topThreeEndpoints, failureMessages);
    }

    private List<String> getTopEntries(Map<String, Long> frequencyMap, int limit) {
        return frequencyMap.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }


}
