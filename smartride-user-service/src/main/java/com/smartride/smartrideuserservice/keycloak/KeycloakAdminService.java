package com.smartride.smartrideuserservice.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Provisionne les utilisateurs dans Keycloak via l'API Admin REST.
 * Permet qu'un utilisateur créé par le user-service puisse se connecter via Keycloak.
 */
@Service
@Slf4j
public class KeycloakAdminService {

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String serverUrl;
    @Value("${keycloak.realm:smartride}")
    private String realm;
    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    private final RestClient rest = RestClient.create();

    /** Récupère un token admin sur le realm master (client admin-cli, password grant). */
    private String obtainAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", adminClientId);
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        Map<?, ?> resp = rest.post()
                .uri(serverUrl + "/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);
        return resp != null ? (String) resp.get("access_token") : null;
    }

    /**
     * Crée l'utilisateur dans Keycloak et lui assigne le rôle realm correspondant.
     * @param realmRole CLIENT, CHAUFFEUR ou ADMIN
     */
    public void createUser(String username, String email, String firstName,
                           String lastName, String password, String realmRole) {
        String token = obtainAdminToken();

        // 1) Création de l'utilisateur
        Map<String, Object> payload = Map.of(
                "username", username,
                "email", email,
                "firstName", firstName == null ? "" : firstName,
                "lastName", lastName == null ? "" : lastName,
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false))
        );

        ResponseEntity<Void> created = rest.post()
                .uri(serverUrl + "/admin/realms/" + realm + "/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        // 2) Récupération de l'id du user via l'en-tête Location
        String location = created.getHeaders().getFirst("Location");
        if (location == null) {
            log.warn("[Keycloak] Utilisateur cree mais Location absent, role non assigne ({})", username);
            return;
        }
        String userId = location.substring(location.lastIndexOf('/') + 1);

        // 3) Récupération de la représentation du rôle realm
        Map<?, ?> roleRep = rest.get()
                .uri(serverUrl + "/admin/realms/" + realm + "/roles/" + realmRole)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        // 4) Assignation du rôle realm à l'utilisateur
        rest.post()
                .uri(serverUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(roleRep))
                .retrieve()
                .toBodilessEntity();

        log.info("[Keycloak] Utilisateur '{}' cree avec le role {}", username, realmRole);
    }
}
