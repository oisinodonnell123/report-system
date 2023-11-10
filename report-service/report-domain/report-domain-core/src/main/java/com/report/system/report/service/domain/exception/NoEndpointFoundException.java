package com.report.system.report.service.domain.exception;

import static com.report.system.report.service.domain.constant.LogParsingConstants.FailureMessages.NO_ENDPOINT_FOUND;
public class NoEndpointFoundException extends ParsingException {
    public NoEndpointFoundException(String logLine) {
        super(NO_ENDPOINT_FOUND + logLine);
    }
}