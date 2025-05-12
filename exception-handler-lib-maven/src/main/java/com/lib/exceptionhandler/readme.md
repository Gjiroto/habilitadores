# Exception Handler Library

Una biblioteca Java para estandarizar y centralizar el manejo de excepciones en microservicios Spring Boot. Esta librería permite transformar las respuestas de error de servicios externos en un formato estandarizado, manteniendo la información relevante pero aplicando códigos y mensajes consistentes.

## Características

- Manejo centralizado de excepciones HTTP
- Estandarización de respuestas de error
- Configuración por código o por archivo de propiedades
- Extracción de información relevante de errores externos
- Soporte para excepciones personalizadas

## Estructura de la Librería

### Clases Principales

| Clase | Descripción |
|-------|-------------|
| `ErrorProperties` | Configuración de los códigos y mensajes de error desde archivos YAML |
| `DefaultErrorRegistry` | Registro predeterminado de códigos y mensajes de error |
| `ExceptionHandlerController` | Controlador global para manejo de excepciones |
| `ErrorResponseExtractor` | Extrae información de errores de servicios externos |
| `StandardErrorResponse` | Modelo estandarizado de respuesta de error |
| `HttpStatusException` | Excepción personalizada para errores HTTP |

## Instalación

Para incluir esta biblioteca en tu proyecto Maven, agrega la siguiente dependencia a tu archivo `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>exception-handler-lib</artifactId>
    <version>1.0.11</version>
</dependency>
```

## Guía de Uso

### 1. Configuración Básica

Para habilitar el manejo de excepciones en tu microservicio, añade el paquete de la librería al escaneo de componentes:

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.tu.microservicio", "com.lib.exceptionhandler"})
public class TuAplicacion {
    public static void main(String[] args) {
        SpringApplication.run(TuAplicacion.class, args);
    }
}
```

### 2. Habilitación de las propiedades de error

Habilita la configuración de propiedades en tu clase `@Configuration`:

```java
@Configuration
@EnableConfigurationProperties(ErrorProperties.class)
public class TuConfiguracion {
    // ...
}
```

### 3. Definición de errores (opcional)

La biblioteca viene con una lista predefinida de errores estándar HTTP en `DefaultErrorRegistry`. Si deseas personalizar los mensajes, puedes definirlos en el mismo archivo


### 4. Uso en tu servicio

#### 4.1 .Crear la configuración de RestTemplate:

Crea una clase de configuración para el RestTemplate que también habilitará las propiedades de error:


```java
@Configuration
@EnableConfigurationProperties(ErrorProperties.class) // Habilitar la carga de propiedades
public class RestTemplateConfig {

    @Autowired
    private ErrorProperties errorProperties;
    @Autowired
    private DefaultErrorRegistry defaultErrorRegistry;

    @PostConstruct
    public void init() {

        Map<String, ErrorProperties.ErrorDetail> errorsMap = errorProperties.getErrors();
        if (errorsMap == null || errorsMap.isEmpty()) {
            errorProperties.setErrors(defaultErrorRegistry.getAllErrors());
        }
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

```
#### 4.2 MOdificacón del servicio
Simplemente permite que las excepciones HTTP se propaguen, y la biblioteca las capturará automáticamente:

```java
@Service
public class CheckService {

    private final RestTemplate restTemplate;
    private final String externalUrl;

    public CheckService(RestTemplate restTemplate, @Value("${external.api.url}") String externalUrl) {
        this.restTemplate = restTemplate;
        this.externalUrl = externalUrl;
    }

    public ResponseEntity<String> checkExternalService() {
        try {
            // Intentamos realizar la llamada al servicio externo
            return restTemplate.getForEntity(externalUrl, String.class);
        } catch (HttpStatusCodeException e) {
            // Ya no manejamos la excepción aquí, simplemente la dejamos propagar
            // para que la maneje nuestro ExceptionHandlerController
            throw e;
        } catch (Exception e) {
            // Para errores generales, lanzamos nuestra excepción personalizada
            throw new HttpStatusException("Error al consultar el servicio externo", "500");
        }
    }
}

```

### 5. Lanzar excepciones personalizadas

Si necesitas lanzar una excepción con un código de error específico:

```java
throw new HttpStatusException(uyyttttttttttttttttttttttg"El recurso solicitado no se pudo encontrar", "404");
```

## Formato de Respuesta Estandarizada

Todas las excepciones manejadas por la biblioteca retornarán una respuesta con la siguiente estructura:

```json
{
  "code": "NOT_FOUND",
  "message": "Recurso no encontrado",
  "timestamp": "2025-05-09T10:15:30.123",
  "details": [
    "El recurso solicitado no se pudo encontrar"
  ]
}
```

## Ejemplos de Uso

### Ejemplo 1: Manejo de errores de un servicio externo

```java
@GetMapping("/data")
public ResponseEntity<Data> getData() {
    // Si el servicio externo retorna un error 404, el ExceptionHandlerController
    // capturará la excepción y generará una respuesta estandarizada
    return dataService.getExternalData();
}
```

### Ejemplo 2: Lanzamiento de errores personalizados

```java
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userRepository.findById(id);
    if (user == null) {
        // Esta excepción será capturada y convertida a una respuesta estandarizada
        throw new HttpStatusException("Usuario no encontrado con ID: " + id, "404");
    }
    return ResponseEntity.ok(user);
}
```

## Errores Predefinidos

La biblioteca incluye los siguientes códigos de error predefinidos:

| Código HTTP | Código de Error | Mensaje Predeterminado |
|-------------|----------------|------------------------|
| 400 | BAD_REQUEST | Solicitud mal formada |
| 401 | UNAUTHORIZED | No autenticado |
| 403 | FORBIDDEN | Sin permisos suficientes |
| 404 | NOT_FOUND | Recurso no encontrado |
| 405 | METHOD_NOT_ALLOWED | Método HTTP no permitido |
| 408 | REQUEST_TIMEOUT | Tiempo de espera agotado |
| 409 | CONFLICT | Conflicto en la solicitud |
| 413 | PAYLOAD_TOO_LARGE | Carga útil muy grande |
| 414 | URI_TOO_LONG | URI demasiado larga |
| 415 | UNSUPPORTED_MEDIA_TYPE | Tipo de medio no soportado |
| 429 | TOO_MANY_REQUESTS | Demasiadas solicitudes |
| 500 | INTERNAL_SERVER_ERROR | Error interno del servidor |
| 501 | NOT_IMPLEMENTED | Método no implementado |
| 502 | BAD_GATEWAY | Respuesta inválida del servidor |
| 503 | SERVICE_UNAVAILABLE | Servicio no disponible |
| 504 | GATEWAY_TIMEOUT | Tiempo de espera agotado |
| 505 | HTTP_VERSION_NOT_SUPPORTED | Versión HTTP no soportada |

## Resolución de Problemas

Si encuentras problemas con la carga de configuraciones desde el archivo YAML, la biblioteca utilizará automáticamente los valores predefinidos en `DefaultErrorRegistry`. Puedes verificar los registros de la aplicación para confirmar qué configuración se está utilizando.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, envía tus pull requests o abre issues para discutir mejoras o reportar bugs.

## Licencia

Esta biblioteca se distribuye bajo la licencia MIT.
