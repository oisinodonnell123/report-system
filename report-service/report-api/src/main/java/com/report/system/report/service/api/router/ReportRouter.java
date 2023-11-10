package com.report.system.report.service.api.router;

import com.report.system.report.service.api.service.ReportApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReportRouter {

    private final ReportApiService reportApiService;

    @Autowired
    public ReportRouter(ReportApiService reportApiService) {
        this.reportApiService = reportApiService;
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions
            .route(RequestPredicates.POST("/report"), reportApiService::generateReport);

    }


}
