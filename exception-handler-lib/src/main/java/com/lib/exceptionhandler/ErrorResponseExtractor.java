package com.lib.exceptionhandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ErrorResponseExtractor {

    private final ObjectMapper objectMapper;

    public ErrorResponseExtractor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Extrae información de error de una excepción HTTP
     */
    public ErrorInfo extractErrorInfo(HttpStatusCodeException ex) {
        try {
            String responseBody = ex.getResponseBodyAsString();
            if (responseBody == null || responseBody.isEmpty()) {
                return new ErrorInfo(null, null, null, null);
            }

            JsonNode rootNode = objectMapper.readTree(responseBody);

            String originalCode = getStringValue(rootNode, "code");
            String originalMessage = getStringValue(rootNode, "message");
            LocalDateTime timestamp = extractTimestamp(rootNode);
            List<String> details = extractDetails(rootNode);

            return new ErrorInfo(originalCode, originalMessage, timestamp, details);
        } catch (IOException e) {
            // Si no podemos parsear la respuesta, devolvemos un ErrorInfo vacío
            return new ErrorInfo(null, null, null, null);
        }
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    private LocalDateTime extractTimestamp(JsonNode rootNode) {
        JsonNode timestampNode = rootNode.get("timestamp");
        if (timestampNode != null && !timestampNode.isNull()) {
            try {
                // Intentamos diferentes formatos de fecha/hora
                String timestampStr = timestampNode.asText();
                try {
                    return LocalDateTime.parse(timestampStr);
                } catch (DateTimeParseException e1) {
                    try {
                        return LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_DATE_TIME);
                    } catch (DateTimeParseException e2) {
                        // Si no podemos parsear, retornamos null
                        return null;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private List<String> extractDetails(JsonNode rootNode) {
        JsonNode detailsNode = rootNode.get("details");
        if (detailsNode != null && detailsNode.isArray()) {
            List<String> details = new ArrayList<>();
            for (JsonNode detail : detailsNode) {
                details.add(detail.asText());
            }
            return details;
        }
        return Collections.emptyList();
    }

    /**
     * Clase interna para representar la información extraída
     */
    public static class ErrorInfo {
        private final String code;
        private final String message;
        private final LocalDateTime timestamp;
        private final List<String> details;

        public ErrorInfo(String code, String message, LocalDateTime timestamp, List<String> details) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
            this.details = details != null ? details : Collections.emptyList();
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public List<String> getDetails() {
            return details;
        }
    }
}
