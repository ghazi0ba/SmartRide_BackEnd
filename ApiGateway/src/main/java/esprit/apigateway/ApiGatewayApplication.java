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
				route("driver-service",
						r->r.path("/api/drivers/**")
								.uri("lb://driver-service"))
				.route("job",r->r.path("/api/users/**")
						.uri("http://localhost:8083"))
				.build();
	}

}
