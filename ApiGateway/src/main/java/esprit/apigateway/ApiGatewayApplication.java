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
				route("candidat",
						r->r.path("/api/trajets/**")
								.uri("http://localhost:8082"))
				.route("job",r->r.path("/api/users/**")
						.uri("http://localhost:8083")).route("payment-service",
						r -> r.path("/api/payments/**")
								.uri("http://localhost:8084"))
				.build();
	}

}
