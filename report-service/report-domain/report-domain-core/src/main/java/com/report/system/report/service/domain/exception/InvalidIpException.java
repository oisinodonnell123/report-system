package com.report.system.report.service.domain.exception;

import static com.report.system.report.service.domain.constant.LogParsingConstants.FailureMessages.INVALID_IP;
public class InvalidIpException extends ParsingException {
    public InvalidIpException(String ip) {
        super(INVALID_IP + ip);
    }
}

