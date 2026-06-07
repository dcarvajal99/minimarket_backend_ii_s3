# MiniMarket Plus — Backend con Seguridad (Desarrollo Backend II, PBY2202)

Backend del caso **"MiniMarket Plus"** al que se integró un esquema completo de
seguridad con **Spring Security 6 + JWT** sobre Spring Boot 3 (Java 17).
Corresponde a la actividad sumativa de la **Semana 3** del ramo *Desarrollo
Backend II* (PBY2202, Duoc UC): *"Integrando seguridad en aplicaciones Backend"*.

## Tecnologías

| Componente | Versión |
|---|---|
| Spring Boot | 3.4.1 |
| Java | 17 |
| Spring Security | 6.x |
| jjwt (JSON Web Token) | 0.12.6 |
| Base de datos | H2 en memoria |
| Build | Maven |

## Arquitectura de seguridad

La autenticación es **stateless**: no se usan sesiones HTTP. El cliente se
autentica una vez en `/api/auth/login` y recibe un **token JWT** firmado (HS256)
que debe enviar en cada petición protegida mediante la cabecera
`Authorization: Bearer <token>`.

```
Cliente ──POST /api/auth/login──► AuthController
                                       │  (AuthenticationManager + BCrypt)
                                       ▼
                                  JwtUtil.generateToken()  ──► token JWT
Cliente ──GET /api/... + Bearer──► JwtAuthenticationFilter
                                       │  (valida firma y expiración)
                                       ▼
                              SecurityContext autenticado ──► @PreAuthorize
```

Componentes clave (paquete `com.minimarket.security`):

- **`config/SecurityConfig`** — cadena de filtros stateless, reglas de
  autorización por URL, `DaoAuthenticationProvider` con BCrypt y registro del
  filtro JWT. Habilita `@PreAuthorize` con `@EnableMethodSecurity`.
- **`util/JwtUtil`** — genera, firma, valida y parsea los tokens (claim de roles).
- **`filter/JwtAuthenticationFilter`** — valida el token en cada petición.
- **`service/CustomUserDetailsService`** — carga usuarios desde la BD.
- **`model/CustomUserDetails`, `LoginRequest`, `LoginResponse`** — modelos de apoyo.
- **`controller/AuthController`** — endpoint `POST /api/auth/login`.
- **`config/DataLoader`** — siembra roles y usuarios con contraseñas BCrypt.
- **`security/xss/*`** + **`config/JacksonConfig`** — sanitización anti-XSS con jsoup.

## Arquitectura de capas y buenas prácticas

La API sigue una arquitectura por capas con separación estricta entidad/DTO:

- **DTOs (`dto/<entidad>/`)** — cada entidad expone un `*Request` (entrada, con
  Bean Validation) y un `*Response` (salida, aplanado para no exponer la entidad
  JPA ni provocar recursión). Un `*Mapper` centraliza la conversión. Las
  entidades JPA nunca se reciben ni devuelven directamente en los controladores.
- **Respuestas uniformes** — toda operación exitosa se envuelve en
  `dto/ApiResponse` (`success`, `message`, `data`, `timestamp`); los errores se
  devuelven como `dto/ErrorResponse` (`status`, `error`, `message`, `path`,
  `timestamp`, `validationErrors`).
- **Manejo global de excepciones** — `exception/GlobalExceptionHandler`
  (`@RestControllerAdvice`) traduce a un formato uniforme: 404
  (`ResourceNotFoundException`), 400 (validación / argumentos), 401
  (credenciales), 403 (acceso denegado) y 500 (genérico), sin filtrar trazas.

## Protección contra amenazas comunes

| Amenaza | Mecanismo implementado |
|---|---|
| **SQL Injection** | Spring Data JPA con consultas parametrizadas (`findByUsername`, `findByNombre`). No se concatena SQL. |
| **XSS** | Sanitización global con **jsoup** (`Safelist.none()`) vía deserializer Jackson: todo String del JSON entrante se limpia. Cabeceras `X-Content-Type-Options` y `Content-Security-Policy`. |
| **CSRF** | Deshabilitado por diseño: API stateless con token Bearer, sin cookies de sesión (CSRF no aplica). |
| **Clickjacking** | Cabecera `X-Frame-Options: SAMEORIGIN`. |
| **Robo de credenciales** | Contraseñas con hash BCrypt; token firmado HMAC-SHA con expiración. HSTS para forzar HTTPS en producción. |
| **Datos malformados** | Bean Validation (`@NotBlank`, `@Positive`…) + `@Valid`, con manejador global de errores. |

## Roles y usuarios de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN` |
| `empleado` | `empleado123` | `ROLE_EMPLEADO` |
| `cliente` | `cliente123` | `ROLE_CLIENTE` |

## Matriz de autorización (resumen)

| Recurso | Lectura | Escritura |
|---|---|---|
| `/api/productos`, `/api/categorias` | ADMIN, EMPLEADO, CLIENTE | ADMIN, EMPLEADO |
| `/api/inventario` | ADMIN, EMPLEADO | ADMIN, EMPLEADO |
| `/api/ventas`, `/api/detalle-ventas` | todos | ADMIN, EMPLEADO |
| `/api/carrito` | ADMIN, CLIENTE | ADMIN, CLIENTE |
| `/api/usuarios` | solo ADMIN | solo ADMIN |
| `/api/auth/**`, `/public/**` | público | — |

## Ejecución

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.
Consola H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, user `sa`).

### Ejemplo de uso

```bash
# 1. Obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Usar el token en un recurso protegido
curl http://localhost:8080/api/productos \
  -H "Authorization: Bearer <TOKEN>"
```

## Resultados de pruebas de seguridad

Ver [`evidencias/resultados_pruebas.txt`](evidencias/resultados_pruebas.txt).
Se verificó: emisión y validación de tokens, rechazo de credenciales inválidas
(401), denegación de acceso sin token (403), control de acceso por rol (403 ante
rol insuficiente) y rechazo de tokens con firma alterada.
