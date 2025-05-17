package com.grupoNoctua.judicial_file_API.config;

import com.grupoNoctua.judicial_file_API.security.JwtAuthenticationFilter;
import com.grupoNoctua.judicial_file_API.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Bean para encriptar passwords con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para obtener AuthenticationManager que Spring Security maneja
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuración principal de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita CSRF porque usamos tokens JWT y no sesiones
                .csrf(csrf -> csrf.disable())

                // Configura CORS con la fuente declarada abajo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configura permisos
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso libre a rutas /auth/**
                        .requestMatchers("/auth/**").permitAll()

                        // Protege rutas de expedientes con autenticación
                        .requestMatchers("/expedientes/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // Stateless: sin sesión HTTP, usamos JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Añade el filtro para validar el JWT antes que el filtro de autenticación por defecto
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración de CORS para permitir peticiones desde el frontend Angular u otros orígenes
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // En desarrollo permitimos todos los orígenes
        config.setAllowedOrigins(Arrays.asList("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (muy importante Authorization para JWT)
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permite enviar cookies (no necesario si no usas sesiones)
        config.setAllowCredentials(true);

        // Fuente para la configuración de CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Aplica esta configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
