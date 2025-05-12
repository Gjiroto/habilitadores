package com.lib.exceptionhandler;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * Registro de errores predefinidos que se utilizan como fallback
 * cuando no se cargan correctamente desde el archivo de propiedades.
 */
@Component
public class DefaultErrorRegistry {

    private final Map<String, ErrorProperties.ErrorDetail> defaultErrors = new HashMap<>();

    @PostConstruct
    public void init() {
        // Inicializamos el mapa con valores predeterminados
        defaultErrors.put("400", new ErrorProperties.ErrorDetail("BAD_REQUEST", "Solicitud mal formada"));
        defaultErrors.put("401", new ErrorProperties.ErrorDetail("UNAUTHORIZED", "No autenticado"));
        defaultErrors.put("403", new ErrorProperties.ErrorDetail("FORBIDDEN", "Sin permisos suficientes"));
        defaultErrors.put("404", new ErrorProperties.ErrorDetail("NOT_FOUND", "Recurso no encontrado"));
        defaultErrors.put("405", new ErrorProperties.ErrorDetail("METHOD_NOT_ALLOWED", "Método HTTP no permitido"));
        defaultErrors.put("408", new ErrorProperties.ErrorDetail("REQUEST_TIMEOUT", "Tiempo de espera agotado"));
        defaultErrors.put("409", new ErrorProperties.ErrorDetail("CONFLICT", "Conflicto en la solicitud"));
        defaultErrors.put("413", new ErrorProperties.ErrorDetail("PAYLOAD_TOO_LARGE", "Carga útil muy grande"));
        defaultErrors.put("414", new ErrorProperties.ErrorDetail("URI_TOO_LONG", "URI demasiado larga"));
        defaultErrors.put("415", new ErrorProperties.ErrorDetail("UNSUPPORTED_MEDIA_TYPE", "Tipo de medio no soportado"));
        defaultErrors.put("429", new ErrorProperties.ErrorDetail("TOO_MANY_REQUESTS", "Demasiadas solicitudes"));
        defaultErrors.put("500", new ErrorProperties.ErrorDetail("INTERNAL_SERVER_ERROR", "Error interno del servidor"));
        defaultErrors.put("501", new ErrorProperties.ErrorDetail("NOT_IMPLEMENTED", "Método no implementado"));
        defaultErrors.put("502", new ErrorProperties.ErrorDetail("BAD_GATEWAY", "Respuesta inválida del servidor"));
        defaultErrors.put("503", new ErrorProperties.ErrorDetail("SERVICE_UNAVAILABLE", "Servicio no disponible"));
        defaultErrors.put("504", new ErrorProperties.ErrorDetail("GATEWAY_TIMEOUT", "Tiempo de espera agotado"));
        defaultErrors.put("505", new ErrorProperties.ErrorDetail("HTTP_VERSION_NOT_SUPPORTED", "Versión HTTP no soportada"));

        System.out.println("DEBUG - DefaultErrorRegistry inicializado con " + defaultErrors.size() + " errores");
    }

    /**
     * Obtiene el detalle de error para un código de estado HTTP.
     *
     * @param statusCode Código de estado HTTP como String
     * @return ErrorDetail o null si no existe
     */
    public ErrorProperties.ErrorDetail getErrorDetail(String statusCode) {
        return defaultErrors.get(statusCode);
    }

    /**
     * Obtiene todos los errores predefinidos
     */
    public Map<String, ErrorProperties.ErrorDetail> getAllErrors() {
        return new HashMap<>(defaultErrors);
    }
}
