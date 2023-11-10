package com.report.system.report.service.domain.constant;

import java.util.regex.Pattern;
import org.apache.commons.validator.routines.InetAddressValidator;

public class LogParsingConstants {

    public static final Pattern LOG_ENTRY_PATTERN = Pattern.compile(
        "^\\s*(\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b|\\[?[A-Fa-f0-9:]+\\]?)\\s.*?\"(GET|POST|PUT|DELETE)\\s+([^\\s\"]+)");
    public static final InetAddressValidator VALIDATOR = InetAddressValidator.getInstance();

    public static class FailureMessages {

        public static final String NO_MATCH_FOR_IP_OR_ENDPOINT = "No match for an IP Address or Endpoint. Log line : ";
        public static final String INVALID_IP = "No match for IPv4 or IPv6 address. Log Line : ";
        public static final String NO_ENDPOINT_FOUND = "No endpoint found. Log line : ";
    }
}