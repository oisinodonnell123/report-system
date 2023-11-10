package com.report.system.report.service.domain.parser;

import com.report.system.report.service.domain.constant.LogParsingConstants;
import com.report.system.report.service.domain.dto.LogEntry;
import com.report.system.report.service.domain.exception.InvalidIpException;
import com.report.system.report.service.domain.exception.NoEndpointFoundException;
import com.report.system.report.service.domain.exception.ParsingException;
import com.report.system.report.service.domain.validation.IpValidator;
import java.util.regex.Matcher;

public class LogParser {

    public static LogEntry parseLogEntry(String logLine) {
        Matcher matcher = LogParsingConstants.LOG_ENTRY_PATTERN.matcher(logLine.trim());
        if (!matcher.find()) {
            throw new ParsingException(
                "No match for an IP Address or Endpoint in log line: " + logLine);
        }

        String rawIp = matcher.group(1);
        String ip = IpValidator.normalizeIp(rawIp);
        String requestPath = matcher.group(3);

        if (!IpValidator.isValid(ip)) {
            throw new InvalidIpException(ip);
        }

        if (isNullOrEmpty(requestPath)) {
            throw new NoEndpointFoundException(logLine);
        }

        return new LogEntry(ip, requestPath);
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
