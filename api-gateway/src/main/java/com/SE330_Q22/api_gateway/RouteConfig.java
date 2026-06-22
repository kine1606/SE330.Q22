package com.SE330_Q22.api_gateway;

import com.SE330_Q22.api_gateway.filter.AuthenticationGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    private final AuthenticationGatewayFilterFactory authFilter;

    public RouteConfig(AuthenticationGatewayFilterFactory authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/user/**")
                        .uri("http://localhost:8084"))
                .route("product-service", r -> r.path("/api/products/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationGatewayFilterFactory.Config())))
                        .uri("http://localhost:8081"))
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationGatewayFilterFactory.Config())))
                        .uri("http://localhost:8082"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationGatewayFilterFactory.Config())))
                        .uri("http://localhost:8083"))
                .route("inventory-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationGatewayFilterFactory.Config())))
                        .uri("http://localhost:8085"))
                .build();
    }
}
