package com.minimarket.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilidad central para la gestion de JSON Web Tokens (JWT).
 *
 * <p>Responsabilidades:
 * <ul>
 *     <li>Generar tokens firmados (HS256) a partir de un {@link UserDetails}.</li>
 *     <li>Validar la firma y la expiracion de los tokens entrantes.</li>
 *     <li>Extraer claims (username, roles, fecha de expiracion).</li>
 * </ul>
 *
 * La clave secreta y el tiempo de expiracion se externalizan en
 * {@code application.properties} para no exponer secretos en el codigo fuente.
 */
@Component
public class JwtUtil {

    /** Clave secreta usada para firmar y verificar los tokens (HMAC-SHA). */
    @Value("${jwt.secret}")
    private String secret;

    /** Tiempo de validez del token en milisegundos. */
    @Value("${jwt.expiration}")
    private long expiration;

    /** Construye la clave HMAC a partir del secreto configurado. */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token firmado para el usuario autenticado, incrustando sus
     * roles como claim "roles" para permitir autorizacion stateless.
     */
    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /** Extrae el nombre de usuario (subject) del token. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Extrae la fecha de expiracion del token. */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Extrae un claim arbitrario aplicando la funcion resolvedora indicada. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Parsea el token verificando su firma y devuelve todos los claims. */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Indica si el token ya expiro. */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida el token: la firma debe ser correcta, el username debe coincidir
     * con el {@link UserDetails} y el token no debe estar expirado.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            // Firma invalida, token malformado o expirado -> token no valido.
            return false;
        }
    }
}
