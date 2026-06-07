package com.minimarket.security.filter;

import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada peticion HTTP (una sola vez por request) para
 * validar el token JWT presente en la cabecera Authorization.
 *
 * <p>Flujo:
 * <ol>
 *     <li>Lee la cabecera {@code Authorization: Bearer <token>}.</li>
 *     <li>Extrae el username y valida el token con {@link JwtUtil}.</li>
 *     <li>Si es valido, carga el usuario y establece la autenticacion en el
 *         {@link SecurityContextHolder}, dejando la peticion autenticada para
 *         el resto de la cadena de filtros.</li>
 * </ol>
 *
 * Al ser un esquema stateless, no se crean ni consultan sesiones HTTP: la
 * identidad del usuario se reconstruye en cada peticion a partir del token.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HEADER);

        // Sin cabecera Bearer: se delega a la cadena (endpoints publicos) o
        // terminara en 401/403 si el recurso requiere autenticacion.
        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(PREFIX.length());
        String username = null;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            // Token malformado o firma invalida: se ignora y continua sin autenticar.
            filterChain.doFilter(request, response);
            return;
        }

        // Solo autentica si hay username y aun no existe autenticacion en el contexto.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
