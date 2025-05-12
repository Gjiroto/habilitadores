package com.lib.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    private final ErrorProperties errorProperties;
    private final DefaultErrorRegistry defaultErrorRegistry;
    private final ErrorResponseExtractor errorExtractor;

    public ExceptionHandlerController(
            ErrorProperties errorProperties,
            DefaultErrorRegistry defaultErrorRegistry,
            ErrorResponseExtractor errorExtractor) {
        this.errorProperties = errorProperties;
        this.defaultErrorRegistry = defaultErrorRegistry;
        this.errorExtractor = errorExtractor;
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<StandardErrorResponse> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        // Obtenemos el código de error HTTP
        String statusCode = String.valueOf(ex.getRawStatusCode());

        // Log para depuración
        System.out.println("DEBUG - HttpStatusCodeException capturada con statusCode: " + statusCode);

        // Verificamos que el mapa de errores no sea nulo antes de acceder
        Map<String, ErrorProperties.ErrorDetail> errorsMap = errorProperties.getErrors();
        if (errorsMap == null || errorsMap.isEmpty()) {
            System.out.println("DEBUG - El mapa de errores de properties está vacío, usando DefaultErrorRegistry");
            errorsMap = defaultErrorRegistry.getAllErrors();
        } else {
            System.out.println("DEBUG - El mapa de errores contiene " + errorsMap.size() + " entradas");
        }

        // Buscamos el detalle del error primero en properties, luego en el registro por defecto
        ErrorProperties.ErrorDetail detail = errorsMap.get(statusCode);
        if (detail == null) {
            detail = defaultErrorRegistry.getErrorDetail(statusCode);
            System.out.println("DEBUG - Usando detalle de DefaultErrorRegistry para statusCode '" + statusCode + "'");
        }

        // Log para depuración del resultado de la búsqueda
        if (detail != null) {
            System.out.println("DEBUG - Se encontró detalle para statusCode '" + statusCode + "': code=" + detail.getCode() + ", message=" + detail.getMessage());
        } else {
            System.out.println("DEBUG - NO se encontró detalle para statusCode '" + statusCode + "'");
        }

        // Si existe el detalle, usamos su código y mensaje; si no, usamos valores por defecto
        String code = (detail != null) ? detail.getCode() : "UNKNOWN_ERROR";
        String message = (detail != null) ? detail.getMessage() : "Error desconocido";

        // Extraemos información del error original
        ErrorResponseExtractor.ErrorInfo errorInfo = errorExtractor.extractErrorInfo(ex);

        // Mantenemos detalles y timestamp del error original si existen
        List<String> details = errorInfo.getDetails().isEmpty() ?
                Collections.singletonList(ex.getMessage()) :
                errorInfo.getDetails();

        LocalDateTime timestamp = errorInfo.getTimestamp() != null ?
                errorInfo.getTimestamp() :
                LocalDateTime.now();

        // Creamos la respuesta estandarizada
        StandardErrorResponse errorResponse = new StandardErrorResponse(
                code,
                message,
                timestamp,
                details
        );

        return ResponseEntity.status(ex.getRawStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<StandardErrorResponse> handleHttpStatusException(HttpStatusException ex) {
        // Obtenemos el código de error personalizado
        String statusCode = ex.getHttpStatusCode();

        // Convertimos el código de string a integer para el status HTTP
        int httpStatus;
        try {
            httpStatus = Integer.parseInt(statusCode);
        } catch (NumberFormatException e) {
            httpStatus = 500; // Por defecto, error interno del servidor
        }

        // Buscamos el detalle primero en properties, luego en el registro por defecto
        Map<String, ErrorProperties.ErrorDetail> errorsMap = errorProperties.getErrors();
        ErrorProperties.ErrorDetail detail = null;

        if (errorsMap != null && !errorsMap.isEmpty()) {
            detail = errorsMap.get(statusCode);
        }

        if (detail == null) {
            detail = defaultErrorRegistry.getErrorDetail(statusCode);
        }

        // Si existe el detalle, usamos su código y mensaje; si no, usamos el mensaje de la excepción
        String code = (detail != null) ? detail.getCode() : "ERROR_" + statusCode;
        String message = (detail != null) ? detail.getMessage() : ex.getMessage();

        // Creamos la respuesta estandarizada
        StandardErrorResponse errorResponse = new StandardErrorResponse(
                code,
                message,
                LocalDateTime.now(),
                Collections.singletonList(ex.getMessage())
        );

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorResponse> handleGenericException(Exception ex) {
        // Para excepciones genéricas no controladas
        Map<String, ErrorProperties.ErrorDetail> errorsMap = errorProperties.getErrors();
        ErrorProperties.ErrorDetail detail = null;

        if (errorsMap != null && !errorsMap.isEmpty()) {
            detail = errorsMap.get("500");
        }

        if (detail == null) {
            detail = defaultErrorRegistry.getErrorDetail("500");
        }

        String code = (detail != null) ? detail.getCode() : "INTERNAL_SERVER_ERROR";
        String message = (detail != null) ? detail.getMessage() : "Error interno del servidor";

        StandardErrorResponse errorResponse = new StandardErrorResponse(
                code,
                message,
                LocalDateTime.now(),
                Collections.singletonList(ex.getMessage())
        );

        return ResponseEntity.status(500).body(errorResponse);
    }
}
