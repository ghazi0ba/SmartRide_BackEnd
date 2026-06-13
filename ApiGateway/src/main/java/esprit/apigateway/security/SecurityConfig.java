package esprit.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Sécurité centralisée au niveau du Gateway.
 * Le Gateway agit comme un OAuth2 Resource Server : il valide les JWT émis par
 * Keycloak (realm "smartride") et applique le contrôle d'accès par rôle sur les routes.
 *
 * Rôles métier (déclarés dans Keycloak) : CLIENT, CHAUFFEUR, ADMIN.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // CORS pour le front (http://localhost:5173)
            .cors(Customizer.withDefaults())
            // API stateless : pas de CSRF
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                // ---- Endpoints publics ----
                .pathMatchers("/api/users/register", "/api/users/login").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(
                        "/v3/api-docs/**", "/*/v3/api-docs/**",
                        "/swagger-ui/**", "/swagger-ui.html", "/webjars/**"
                ).permitAll()

                // ---- Contrôle d'accès par rôle ----
                .pathMatchers("/api/drivers/**").hasAnyRole("CHAUFFEUR", "ADMIN")
                .pathMatchers("/payments/**").hasAnyRole("CLIENT", "ADMIN")
                .pathMatchers("/api/reservations/**").hasAnyRole("CLIENT", "CHAUFFEUR", "ADMIN")
                .pathMatchers("/api/ratings/**").hasAnyRole("CLIENT", "CHAUFFEUR", "ADMIN")
                .pathMatchers("/smartride_trajet/api/trajets/**").hasAnyRole("CLIENT", "CHAUFFEUR", "ADMIN")
                .pathMatchers("/api/users/**").hasRole("ADMIN")

                // ---- Tout le reste nécessite une authentification ----
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakRolesConverter()))
            );

        return http.build();
    }

    /**
     * Autorise le front (SPA) à appeler le Gateway depuis le navigateur.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Convertit les rôles Keycloak en autorités Spring Security.
     * Keycloak place les rôles dans :
     *   - realm_access.roles               (rôles de realm)
     *   - resource_access.{client}.roles   (rôles de client)
     * On les préfixe par ROLE_ pour que hasRole("X") fonctionne.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> keycloakRolesConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::extractRoles);
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    @SuppressWarnings("unchecked")
    private static Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new HashSet<>();

        // Rôles de realm
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        }

        // Rôles de client (resource_access.{client}.roles)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            for (Object clientEntry : resourceAccess.values()) {
                if (clientEntry instanceof Map<?, ?> client
                        && client.get("roles") instanceof Collection<?> roles) {
                    roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
                }
            }
        }

        return authorities;
    }
}
