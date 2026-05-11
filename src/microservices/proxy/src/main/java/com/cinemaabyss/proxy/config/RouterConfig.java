package com.cinemaabyss.proxy.config;

import com.cinemaabyss.proxy.handler.ProxyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProxyHandler proxyHandler) {
        return RouterFunctions.route()
                .GET("/health", proxyHandler::health)
                .path("/api/movies", builder -> builder
                        .GET("", proxyHandler::proxyMovies)
                        .POST("", proxyHandler::proxyMovies)
                )
                .path("/api/events", builder -> builder
                        .GET("/**", proxyHandler::proxyEvents)
                        .POST("/**", proxyHandler::proxyEvents)
                )
                .path("/api", builder -> builder
                        .GET("/**", proxyHandler::proxyMonolith)
                        .POST("/**", proxyHandler::proxyMonolith)
                        .PUT("/**", proxyHandler::proxyMonolith)
                        .DELETE("/**", proxyHandler::proxyMonolith)
                )
                .build();
    }
}
