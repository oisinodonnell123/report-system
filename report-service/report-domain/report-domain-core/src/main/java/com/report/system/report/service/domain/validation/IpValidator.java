package com.report.system.report.service.domain.validation;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.validator.routines.InetAddressValidator;

public class IpValidator {

    private static final InetAddressValidator validator = InetAddressValidator.getInstance();

    public static boolean isValid(String ip) {
        return validator.isValid(ip);
    }

    public static String normalizeIp(String ip) {
        return Arrays.stream(ip.split("\\."))
            .map(octet -> String.valueOf(Integer.parseInt(octet)))
            .collect(Collectors.joining("."));
    }
}

