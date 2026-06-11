package esprit.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class ApiGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	@Bean
	public RouteLocator getRoute(RouteLocatorBuilder builder) {
		return builder.routes().
				route("trajet",
						r->r.path("/api/trajets/**")
								.uri("lb://trajet-s"))
				.route("user",r->r.path("/api/users/**")
						.uri("lb://smartride-user-service"))
                .route("reservation",r->r.path("/api/reservations/**")
                        .uri("lb://smartride-reservation-service"))
				.build();
	}

}
