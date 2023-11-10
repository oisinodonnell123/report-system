package com.report.system.report.service.api.service;

import com.report.system.report.service.api.FileProcessingHelper;
import com.report.system.report.service.domain.ReportHandler;
import com.report.system.report.service.domain.valueObject.LogReport;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ReportApiServiceImpl implements ReportApiService {

    private final ReportHandler reportHandler;

    @Autowired
    public ReportApiServiceImpl(ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
    }

    public Mono<ServerResponse> generateReport(ServerRequest request) {
        return request.multipartData()
            .map(multipart -> multipart.getFirst("file"))
            .cast(FilePart.class)
            .flatMap(this::validateAndProcessFile)
            .flatMap(logLines -> reportHandler.processLog(logLines))
            .flatMap(this::buildOkResponse)
            .onErrorResume(this::handleErrorResponse);
    }

    private Mono<List<String>> validateAndProcessFile(FilePart filePart) {
        if (!isValidFileType(filePart.filename())) {
            return Mono.error(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type"));
        }
        return FileProcessingHelper.processFilePart(filePart);
    }

    private boolean isValidFileType(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        return lowerCaseFilename.endsWith(".log") || lowerCaseFilename.endsWith(".txt");
    }

    private Mono<ServerResponse> buildOkResponse(LogReport report) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(report));
    }

    private Mono<ServerResponse> handleErrorResponse(Throwable e) {
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR; // Default to internal server error
        String message = "An unexpected error occurred";     // Default error message

        if (e instanceof ResponseStatusException) {
            ResponseStatusException ex = (ResponseStatusException) e;
            status = ex.getStatusCode();
            message = ex.getReason();
        } else {
            log.error("Unexpected error occurred", e); // Uncomment if you have a logger
        }

        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ErrorResponse(message)));
    }

    class ErrorResponse {

        private String error;

        public ErrorResponse() {
        }

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}

