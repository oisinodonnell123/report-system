package com.report.system.report.service.api.exception.handler;

import com.report.system.report.service.api.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class ReportServiceExceptionHandler {


    @ExceptionHandler(FileProcessingException.class)
    public Mono<ServerResponse> handleFileProcessingException(FileProcessingException ex) {
        log.error("File processing error: {}", ex.getMessage(), ex);
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ErrorResponse("File processing error")));
    }


    public class ErrorResponse {
        private String error;

        public ErrorResponse() {}

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

}
