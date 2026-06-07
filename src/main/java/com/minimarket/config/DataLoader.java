package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Carga datos iniciales de seguridad al arrancar la aplicacion: los tres roles
 * del negocio (ADMIN, EMPLEADO, CLIENTE) y un usuario de ejemplo por cada rol.
 *
 * <p>Las contrasenas se almacenan cifradas con BCrypt; nunca en texto plano.
 * Los roles se nombran con el prefijo {@code ROLE_} requerido por Spring
 * Security para que {@code hasRole(...)} y {@code @PreAuthorize} funcionen.
 *
 * <p>Usuarios sembrados (usuario / contrasena):
 * <ul>
 *     <li>admin / admin123 -> ROLE_ADMIN</li>
 *     <li>empleado / empleado123 -> ROLE_EMPLEADO</li>
 *     <li>cliente / cliente123 -> ROLE_CLIENTE</li>
 * </ul>
 */
@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(RolRepository rolRepository,
                                      UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Evita duplicar datos en reinicios con la misma BD.
            if (usuarioRepository.count() > 0) {
                return;
            }

            Rol rolAdmin = crearRol(rolRepository, "ROLE_ADMIN");
            Rol rolEmpleado = crearRol(rolRepository, "ROLE_EMPLEADO");
            Rol rolCliente = crearRol(rolRepository, "ROLE_CLIENTE");

            crearUsuario(usuarioRepository, passwordEncoder, "admin", "admin123", Set.of(rolAdmin));
            crearUsuario(usuarioRepository, passwordEncoder, "empleado", "empleado123", Set.of(rolEmpleado));
            crearUsuario(usuarioRepository, passwordEncoder, "cliente", "cliente123", Set.of(rolCliente));
        };
    }

    private Rol crearRol(RolRepository rolRepository, String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            return rolRepository.save(rol);
        });
    }

    private void crearUsuario(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              String username, String rawPassword, Set<Rol> roles) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(rawPassword)); // hash BCrypt
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
    }
}
