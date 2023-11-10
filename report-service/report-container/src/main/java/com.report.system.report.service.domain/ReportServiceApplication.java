package com.report.system.report.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.report.system")
public class ReportServiceApplication {
    public static void main(String[] args) {
         SpringApplication.run(ReportServiceApplication.class, args);
    }
}

