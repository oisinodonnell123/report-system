package com.report.system.report.service.domain;

import com.report.system.report.service.domain.service.ReportDomainService;
import com.report.system.report.service.domain.valueObject.LogReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportHandlerTest {

    @Mock
    private ReportDomainService reportDomainService;

    private ReportHandler reportHandler;

    @BeforeEach
    void setUp() {
        reportHandler = new ReportHandler(reportDomainService);
    }

    @Test
    @DisplayName("Should process log entries successfully")
    void shouldProcessLogEntriesSuccessfully() {
        List<String> sampleLogLines = Collections.singletonList("177.71.128.21 - - [10/Jul/2018:22:21:28 +0200] \"GET /intranet-analytics/ HTTP/1.1\" 200 3574");
        LogReport expectedReport = new LogReport(1, Collections.singletonList("177.71.128.21"), Collections.singletonList("/intranet-analytics/"), Collections.emptyList());

        when(reportDomainService.processLogEntries(any(List.class))).thenReturn(expectedReport);

        Mono<LogReport> result = reportHandler.processLog(sampleLogLines);

        StepVerifier.create(result)
            .expectNext(expectedReport)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle log processing with errors")
    void shouldHandleLogProcessingWithErrors() {
        List<String> sampleLogLines = Collections.singletonList("Invalid log entry");
        List<String> errorMessages = Collections.singletonList("Parsing exception occurred");
        LogReport expectedReport = new LogReport(0, Collections.emptyList(), Collections.emptyList(), errorMessages);

        when(reportDomainService.processLogEntries(any(List.class))).thenReturn(expectedReport);

        Mono<LogReport> result = reportHandler.processLog(sampleLogLines);

        StepVerifier.create(result)
            .consumeNextWith(logReport -> {
                Assertions.assertTrue(logReport.getErrorMessages().contains("Parsing exception occurred"));
                Assertions.assertEquals(0, logReport.getDistinctIpsCount());
            })
            .verifyComplete();
    }
}