package com.report.system.report.service.domain;

import com.report.system.report.service.domain.service.ReportDomainService;
import com.report.system.report.service.domain.valueObject.LogReport;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReportHandler.class);
    private final ReportDomainService reportDomainService;

    public ReportHandler(ReportDomainService reportDomainService) {
        this.reportDomainService = reportDomainService;
    }

    public Mono<LogReport> processLog(List<String> logLines) {
        return Mono.fromCallable(() -> reportDomainService.processLogEntries(logLines))
            .doOnSuccess(logReport -> logFailureMessages(logReport.getErrorMessages()));
    }

    private void logFailureMessages(List<String> failureMessages) {
        if (failureMessages != null && !failureMessages.isEmpty()) {
            failureMessages.forEach(message -> logger.warn("Log parsing issue: {}", message));
        }
    }
}
