package com.report.system.report.service.api.service;

import com.report.system.report.service.api.router.ReportRouter;
import com.report.system.report.service.domain.ReportHandler;
import com.report.system.report.service.domain.valueObject.LogReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ReportRouter.class, ReportApiServiceImpl.class})
@WebFluxTest
class ReportApiServiceTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReportHandler reportHandler;
    private final String validLogFileName = "test.log";
    private final String sampleLogContent =
        "177.71.128.21 - - [10/Jul/2018:22:21:28 +0200] \"GET /intranet-analytics/ HTTP/1.1\" 200 3574 \"-\" \"Mozilla/5.0 (X11; U; Linux x86_64; fr-FR) AppleWebKit/534.7 (KHTML, like Gecko) Epiphany/2.30.6 Safari/534.7\"\n"
            + "168.41.191.40 - - [09/Jul/2018:10:11:30 +0200] \"GET http://example.net/faq/ HTTP/1.1\" 200 3574 \"-\" \"Mozilla/5.0 (Linux; U; Android 2.3.5; en-us; HTC Vision Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1\"";
    private final LogReport sampleLogReport = new LogReport(
        0,
        Arrays.asList("168.41.191.40", "177.71.128.21", "50.112.0.11"),
        Arrays.asList("/docs/manage-websites/", "/blog/2018/08/survey-your-opinion-matters/",
            "/newsletter/"),
        List.of()
    );

    @BeforeEach
    void setUp() {

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(
            sampleLogContent.getBytes(StandardCharsets.UTF_8));

        FilePart filePart = Mockito.mock(FilePart.class);
        when(filePart.filename()).thenReturn(validLogFileName);
        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(filePart.headers()).thenReturn(new HttpHeaders());

        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        multipartData.add("file", filePart);
        when(serverRequest.multipartData()).thenReturn(Mono.just(multipartData));

        when(reportHandler.processLog(ArgumentMatchers.anyList())).thenReturn(
            Mono.just(sampleLogReport));
    }

    private WebTestClient.ResponseSpec executePostWithFilePart(String fileName,
        String fileContent) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", fileContent.getBytes(StandardCharsets.UTF_8))
            .header("Content-Disposition",
                "form-data; name=\"file\"; filename=\"" + fileName + "\"");

        return webTestClient
            .post()
            .uri("/report")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange();
    }

    @Test
    @DisplayName("Should generate report successfully with valid log file")
    void shouldGenerateReportWithValidLogFile() {
        executePostWithFilePart(validLogFileName, sampleLogContent)
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.distinctIpsCount").isEqualTo(sampleLogReport.getDistinctIpsCount())
            .jsonPath("$.topIps[0]").isEqualTo(sampleLogReport.getTopIps().get(0))
            .jsonPath("$.topEndpoints[0]").isEqualTo(sampleLogReport.getTopEndpoints().get(0))
            .jsonPath("$.errorMessages").isEmpty();
    }

    @Test
    @DisplayName("Should return bad request for invalid file type")
    void shouldReturnBadRequestForInvalidFileType() {
        String invalidFileName = "invalid_file.xyz";
        String fileContent = "This is not a log file.";
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileContent.getBytes()) {
            @Override
            public String getFilename() {
                return invalidFileName;
            }
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", byteArrayResource);

        webTestClient
            .post()
            .uri("/report")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should handle empty file content gracefully")
    void shouldHandleEmptyFileContentGracefully() {
        executePostWithFilePart(validLogFileName, "")
            .expectStatus().isOk();
    }

}