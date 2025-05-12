package com.lib.exceptionhandler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "errors")
public class ErrorProperties {

    // Necesitamos asegurarnos que Spring pueda mapear correctamente desde el YAML
    // Las propiedades de tipo Map necesitan un setter explícito para el binding
    private Map<String, ErrorDetail> errors = new HashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("DEBUG - ErrorProperties inicializado");
        System.out.println("DEBUG - Mapa de errores contiene " + (errors != null ? errors.size() : "NULL") + " entradas");
        if (errors != null && !errors.isEmpty()) {
            System.out.println("DEBUG - Errores configurados: " + errors.keySet());
            for (Map.Entry<String, ErrorDetail> entry : errors.entrySet()) {
                System.out.println("DEBUG - Error '" + entry.getKey() + "': " +
                        (entry.getValue() != null ?
                                "code=" + entry.getValue().getCode() + ", message=" + entry.getValue().getMessage()
                                : "NULL"));
            }
        }
    }

    public Map<String, ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, ErrorDetail> errors) {
        this.errors = errors != null ? errors : new HashMap<>();
        System.out.println("DEBUG - setErrors llamado, ahora el mapa contiene " + this.errors.size() + " entradas");
    }

    public static class ErrorDetail {
        private String code;
        private String message;

        public ErrorDetail() {
            // Constructor vacío necesario para la deserialización
        }

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
