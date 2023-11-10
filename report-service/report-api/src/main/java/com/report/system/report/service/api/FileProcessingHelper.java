package com.report.system.report.service.api;

import com.report.system.report.service.api.exception.FileProcessingException;
import java.util.stream.Collectors;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class FileProcessingHelper {

    public static Mono<List<String>> processFilePart(FilePart filePart) {
        return filePart.content()
            .<String>handle((dataBuffer, sink) -> {
                try {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    sink.next(new String(bytes, StandardCharsets.UTF_8));
                } catch (Exception e) {
                    sink.error(new FileProcessingException("Error processing file part", e));
                } finally {
                    DataBufferUtils.release(dataBuffer);
                }
            })
            .collect(Collectors.joining())
            .map(content -> sanitizeContent(content))
            .map(content -> Arrays.asList(content.split("\\r?\\n")))
            .onErrorMap(Exception.class, e -> {
                if (e instanceof FileProcessingException) {
                    return e; // Rethrow if it's already our custom exception
                }
                return new FileProcessingException("Failed to process file part", e);
            });
    }

    private static String sanitizeContent(String content) {
        return content.replace("<", " ")
            .replace(">", " ");
    }
}