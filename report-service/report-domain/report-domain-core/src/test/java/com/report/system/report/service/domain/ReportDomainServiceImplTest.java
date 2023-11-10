package com.report.system.report.service.domain;

import com.report.system.report.service.domain.dto.LogEntry;
import com.report.system.report.service.domain.exception.ParsingException;
import com.report.system.report.service.domain.parser.LogParser;
import com.report.system.report.service.domain.valueObject.LogReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class ReportDomainServiceImplTest {

    private ReportDomainServiceImpl reportDomainService;

    @BeforeEach
    void setUp() {
        reportDomainService = new ReportDomainServiceImpl();
    }

    @Test
    @DisplayName("Should process log entries successfully")
    void shouldProcessLogEntriesSuccessfully() {
        try (MockedStatic<LogParser> mockedLogParser = mockStatic(LogParser.class)) {
            mockedLogParser.when(() -> LogParser.parseLogEntry(Mockito.anyString()))
                .thenReturn(new LogEntry("123.123.123.123", "/test"));

            List<String> logLines = Arrays.asList(
                "123.123.123.123 - - [10/Jul/2018:22:21:28 +0200] \"GET /test HTTP/1.1\" 200 3574"
            );

            LogReport logReport = reportDomainService.processLogEntries(logLines);

            assertEquals(1, logReport.getDistinctIpsCount());
            assertEquals(1, logReport.getTopIps().size());
            assertEquals("123.123.123.123", logReport.getTopIps().get(0));
            assertEquals(1, logReport.getTopEndpoints().size());
            assertEquals("/test", logReport.getTopEndpoints().get(0));
            assertTrue(logReport.getErrorMessages().isEmpty());
        }
    }

    @Test
    @DisplayName("Should accurately count frequencies of IPs and endpoints")
    void shouldAccuratelyCountFrequencies() {
        try (MockedStatic<LogParser> mockedLogParser = mockStatic(LogParser.class)) {
            mockedLogParser.when(() -> LogParser.parseLogEntry(Mockito.contains("/test1")))
                .thenReturn(new LogEntry("123.123.123.123", "/test1"));
            mockedLogParser.when(() -> LogParser.parseLogEntry(Mockito.contains("/test2")))
                .thenReturn(new LogEntry("123.123.123.124", "/test2"));
            mockedLogParser.when(() -> LogParser.parseLogEntry(Mockito.contains("/test3")))
                .thenReturn(new LogEntry("123.123.123.125", "/test3"));

            List<String> logLines = Arrays.asList(
                "123.123.123.123 /test1",
                "123.123.123.123 /test2",
                "123.123.123.124 /test1",
                "123.123.123.124 /test2",
                "123.123.123.125 /test3"
            );

            LogReport logReport = reportDomainService.processLogEntries(logLines);

            assertEquals(3, logReport.getDistinctIpsCount());
            assertEquals(3, logReport.getTopIps().size());
            assertTrue(logReport.getTopIps().containsAll(Arrays.asList("123.123.123.123", "123.123.123.124", "123.123.123.125")));
            assertEquals(3, logReport.getTopEndpoints().size());
            assertTrue(logReport.getTopEndpoints().containsAll(Arrays.asList("/test1", "/test2", "/test3")));
            assertTrue(logReport.getErrorMessages().isEmpty());
        }
    }



    @Test
    @DisplayName("Should handle parsing exceptions and report failure messages")
    void shouldHandleParsingExceptionsAndReportFailureMessages() {
        try (MockedStatic<LogParser> mockedLogParser = mockStatic(LogParser.class)) {
            mockedLogParser.when(() -> LogParser.parseLogEntry(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String logLine = invocation.getArgument(0);
                    if (logLine.contains("Invalid")) {
                        throw new ParsingException("No match for an IP Address or Endpoint in log line: " + logLine);
                    }
                    return new LogEntry("123.123.123.123", "/test");
                });

            List<String> logLines = Arrays.asList(
                "123.123.123.123 /test",
                "Invalid log line"
            );

            LogReport logReport = reportDomainService.processLogEntries(logLines);

            assertEquals(1, logReport.getDistinctIpsCount());
            assertEquals(1, logReport.getTopEndpoints().size());
            assertEquals("/test", logReport.getTopEndpoints().get(0));
            assertEquals(1, logReport.getErrorMessages().size());
            assertTrue(logReport.getErrorMessages().get(0).contains("No match for an IP Address or Endpoint in log line: Invalid log line"));
        }
    }

}
