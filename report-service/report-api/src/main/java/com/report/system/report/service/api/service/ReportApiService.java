package com.report.system.report.service.api.service;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ReportApiService {

    Mono<ServerResponse> generateReport(ServerRequest request);

}
