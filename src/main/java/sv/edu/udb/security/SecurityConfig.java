package sv.edu.udb.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                // Devuelve JSON 401 en lugar de redirigir al login de Spring
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    String body = new ObjectMapper().writeValueAsString(
                        Map.of("error", "No autorizado", "message", "Token inválido o expirado")
                    );
                    response.getWriter().write(body);
                })
                // Devuelve JSON 403 en lugar de página de error
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    String body = new ObjectMapper().writeValueAsString(
                        Map.of("error", "Acceso denegado", "message", "No tienes permiso para esta acción")
                    );
                    response.getWriter().write(body);
                })
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/api/clientes/registro").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/soporte").permitAll()
                .requestMatchers("/api/productos/disponibles").permitAll()
                .requestMatchers("/api/productos/buscar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias", "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sucursales", "/api/sucursales/**").permitAll()
                .requestMatchers("/frontend/**").permitAll()
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/pages/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "CAJERO", "CLIENTE")
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers("/api/categorias/**").hasRole("ADMIN")
                .requestMatchers("/api/sucursales/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/soporte", "/api/soporte/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/soporte/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/soporte/**").hasRole("ADMIN")
                .requestMatchers("/api/proveedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN", "CAJERO")
                .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "CLIENTE")
                .requestMatchers("/api/inventario/**").hasAnyRole("ADMIN", "CAJERO")
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "CAJERO", "CLIENTE")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
