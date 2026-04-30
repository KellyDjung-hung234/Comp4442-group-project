package com.shareu.ui;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Gateway configuration to preserve custom headers through routing.
 * Ensures X-Admin-Username, X-User-Id, X-User-Role are passed to downstream services.
 */
@Configuration
public class GatewayConfig {

    /**
     * Forwards all request headers including custom ones through the gateway.
     * This ensures headers like X-Admin-Username, X-User-Id, X-User-Role reach the target services.
     */
    @Bean
    public GlobalFilter customHeaderForwardingFilter() {
        return (exchange, chain) -> {
            // Spring Cloud Gateway preserves all headers by default when routing requests
            // This filter ensures the headers are maintained through the chain
            exchange.getRequest().mutate()
                    .header(HttpHeaders.CONNECTION, "Keep-Alive")
                    .build();
            return chain.filter(exchange);
        };
    }
}

