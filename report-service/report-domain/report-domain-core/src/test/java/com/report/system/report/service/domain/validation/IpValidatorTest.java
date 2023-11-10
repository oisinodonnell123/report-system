package com.report.system.report.service.domain.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IpValidatorTest {

    @Test
    @DisplayName("Should validate valid IPv4 addresses")
    void shouldValidateValidIPv4Addresses() {
        Assertions.assertTrue(IpValidator.isValid("192.168.1.1"));
        Assertions.assertTrue(IpValidator.isValid("127.0.0.1"));
    }

    @Test
    @DisplayName("Should validate valid IPv6 addresses")
    void shouldValidateValidIPv6Addresses() {
        Assertions.assertTrue(IpValidator.isValid("0:0:0:0:0:0:0:1"));
        Assertions.assertTrue(IpValidator.isValid("2001:db8:85a3:0:0:8a2e:370:7334"));
    }

    @Test
    @DisplayName("Should invalidate invalid IP addresses")
    void shouldInvalidateInvalidIPAddresses() {
        Assertions.assertFalse(IpValidator.isValid("256.256.256.256"));
        Assertions.assertFalse(IpValidator.isValid("invalid_ip"));
    }

    @Test
    @DisplayName("Should normalize valid IPv4 addresses")
    void shouldNormalizeValidIPv4Addresses() {
        Assertions.assertEquals("192.168.1.1", IpValidator.normalizeIp("192.168.001.001"));
        Assertions.assertEquals("10.0.0.1", IpValidator.normalizeIp("010.000.000.001"));
    }
}
