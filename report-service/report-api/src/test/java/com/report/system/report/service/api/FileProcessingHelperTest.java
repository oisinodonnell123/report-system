package com.report.system.report.service.api;

import com.report.system.report.service.api.exception.FileProcessingException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileProcessingHelperTest {

    @Mock
    private FilePart mockFilePart;
    @Mock
    private DataBuffer mockDataBuffer;

    @BeforeEach
    public void setUp() {
        when(mockFilePart.content()).thenReturn(Flux.just(mockDataBuffer));
    }

    @Test
    public void processFilePart_Successful() {
        String testContent = "127.0.0.1 - - [09/Jul/2018:10:11:30 +0200] \"GET / HTTP/1.1\" 200";
        byte[] contentBytes = testContent.getBytes(StandardCharsets.UTF_8);

        when(mockDataBuffer.readableByteCount()).thenReturn(contentBytes.length);
        when(mockDataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(contentBytes, 0, buffer, 0, contentBytes.length);
            return mockDataBuffer;
        });

        when(mockFilePart.content()).thenReturn(Flux.just(mockDataBuffer));

        StepVerifier.create(FileProcessingHelper.processFilePart(mockFilePart))
            .expectNextMatches(lines -> lines.contains(testContent))
            .verifyComplete();

    }

    @Test
    public void processFilePart_Error() {
        when(mockDataBuffer.readableByteCount()).thenThrow(RuntimeException.class);

        StepVerifier.create(FileProcessingHelper.processFilePart(mockFilePart))
            .expectError(FileProcessingException.class)
            .verify();
    }

    @Test
    public void sanitizeContent_RemovesAngleBrackets() {
        String contentWithBrackets = "127.0.0.1 - - [09/Jul/2018:10:11:30 +0200] \"GET <script>alert('xss')</script> HTTP/1.1\" 200";
        String expectedContent = "127.0.0.1 - - [09/Jul/2018:10:11:30 +0200] \"GET  script alert('xss') /script  HTTP/1.1\" 200";
        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(
            contentWithBrackets.getBytes(StandardCharsets.UTF_8));

        when(mockFilePart.content()).thenReturn(Flux.just(dataBuffer));
        Mono<List<String>> result = FileProcessingHelper.processFilePart(mockFilePart);

        StepVerifier.create(result)
            .expectNextMatches(lines -> lines.contains(expectedContent))
            .verifyComplete();
    }


}
