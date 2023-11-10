package com.report.system.report.service.domain;


import com.report.system.report.service.domain.service.ReportDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ReportDomainService reportDomainService() {
        return new ReportDomainServiceImpl();
    }

}
