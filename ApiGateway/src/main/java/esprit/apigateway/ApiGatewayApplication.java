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
		return builder.routes()

				.route("trajet-service",
						r -> r.path("/api/trajets/**")
								.uri("lb://TRAJET-SERVICE"))

				.route("user-service",
						r -> r.path("/api/users/**")
								.uri("lb://USER-SERVICE"))

				.route("payment-service",
						r -> r.path("/payments/**")
								.uri("lb://PAYMENT-SERVICE"))

				.build();
	}

}
