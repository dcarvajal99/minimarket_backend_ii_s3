package com.minimarket.security.config;

import com.minimarket.security.filter.JwtAuthenticationFilter;
import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracion central de Spring Security para una API REST stateless con JWT.
 *
 * <p>Decisiones de diseno:
 * <ul>
 *     <li><b>Stateless</b>: no se crean sesiones HTTP; la identidad viaja en el token.</li>
 *     <li><b>Sin formLogin</b>: la autenticacion se realiza por el endpoint
 *         {@code /api/auth/login}, no por un formulario web.</li>
 *     <li>El {@link JwtAuthenticationFilter} se ejecuta antes del filtro estandar
 *         de usuario/contrasena para autenticar a partir del token.</li>
 *     <li>Autorizacion por roles a nivel de URL y, complementariamente, a nivel
 *         de metodo via {@code @PreAuthorize} ({@link EnableMethodSecurity}).</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity // habilita @PreAuthorize / @PostAuthorize en los controladores
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // En una API stateless con JWT, CSRF no aplica (no se usan cookies de sesion).
                .csrf(AbstractHttpConfigurer::disable)
                // Sin estado de sesion en el servidor: cada peticion se autentica por token.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publicos: login, consola H2 y documentacion Swagger (desarrollo).
                        .requestMatchers("/api/auth/**", "/public/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // La gestion de usuarios queda reservada al rol ADMIN.
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        // El resto de la API requiere autenticacion (control fino via @PreAuthorize).
                        .anyRequest().authenticated()
                )
                // Cabeceras de seguridad HTTP (defensa contra XSS, clickjacking y sniffing).
                .headers(headers -> headers
                        // Permite que la consola H2 se renderice dentro de un frame del mismo origen.
                        .frameOptions(frame -> frame.sameOrigin())
                        // Evita que el navegador "adivine" el tipo de contenido (anti MIME-sniffing).
                        .contentTypeOptions(cto -> {})
                        // Content-Security-Policy: restringe el origen de los recursos (mitiga XSS).
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'; frame-ancestors 'self'"))
                        // HSTS: fuerza HTTPS en navegadores compatibles (relevante en produccion).
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                );

        // Provider de autenticacion basado en BD + BCrypt.
        http.authenticationProvider(authenticationProvider());

        // Inserta el filtro JWT antes del filtro de usuario/contrasena.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provider que delega la carga de usuarios en CustomUserDetailsService y
     * la verificacion de contrasenas en BCryptPasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // hash unidireccional con salt automatico
    }
}
