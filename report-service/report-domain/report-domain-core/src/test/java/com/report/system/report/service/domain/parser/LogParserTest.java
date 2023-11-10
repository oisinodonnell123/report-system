package com.report.system.report.service.domain.parser;

import com.report.system.report.service.domain.dto.LogEntry;
import com.report.system.report.service.domain.exception.InvalidIpException;
import com.report.system.report.service.domain.exception.NoEndpointFoundException;
import com.report.system.report.service.domain.exception.ParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LogParserTest {

    @Test
    @DisplayName("Should parse valid log entry correctly")
    void shouldParseValidLogEntryCorrectly() {
        String logLine = "177.71.128.21 - - [10/Jul/2018:22:21:28 +0200] \"GET /intranet-analytics/ HTTP/1.1\" 200 3574";
        LogEntry result = LogParser.parseLogEntry(logLine);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("177.71.128.21", result.getIp());
        Assertions.assertEquals("/intranet-analytics/", result.getEndpoint());
    }

    @Test
    @DisplayName("Should throw InvalidIpException for invalid IP address")
    void shouldThrowInvalidIpExceptionForInvalidIP() {
        String logLine = "999.999.999.999 - - [10/Jul/2018:22:21:28 +0200] \"GET /intranet-analytics/ HTTP/1.1\" 200 3574";
        Assertions.assertThrows(InvalidIpException.class, () -> LogParser.parseLogEntry(logLine));
    }

    @Test
    @DisplayName("Should throw ParsingException for log entries that don't match the pattern")
    void shouldThrowParsingExceptionForNonMatchingLogEntries() {
        String logLine = "This is not a valid log entry";
        Assertions.assertThrows(ParsingException.class, () -> LogParser.parseLogEntry(logLine));
    }
}

