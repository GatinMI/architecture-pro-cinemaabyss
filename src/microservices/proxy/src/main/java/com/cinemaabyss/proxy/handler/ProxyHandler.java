package com.cinemaabyss.proxy.handler;

import com.cinemaabyss.proxy.config.ProxyConfig;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class ProxyHandler {

    private final ProxyConfig proxyConfig;
    private final WebClient webClient;

    public ProxyHandler(ProxyConfig proxyConfig, WebClient.Builder webClientBuilder) {
        this.proxyConfig = proxyConfig;
        this.webClient = webClientBuilder.build();
    }

    public Mono<ServerResponse> health(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Strangler Fig Proxy is healthy");
    }

    public Mono<ServerResponse> proxyMovies(ServerRequest request) {
        String targetUrl = resolveMoviesTarget();
        return forwardRequest(request, targetUrl, "/api/movies");
    }

    public Mono<ServerResponse> proxyMonolith(ServerRequest request) {
        String path = request.path();
        return forwardRequest(request, proxyConfig.getMonolithUrl(), path);
    }

    public Mono<ServerResponse> proxyEvents(ServerRequest request) {
        String path = request.path();
        return forwardRequest(request, proxyConfig.getEventsServiceUrl(), path);
    }

    private String resolveMoviesTarget() {
        if (proxyConfig.isGradualMigration()) {
            int random = ThreadLocalRandom.current().nextInt(100);
            if (random < proxyConfig.getMoviesMigrationPercent()) {
                return proxyConfig.getMoviesServiceUrl();
            }
        }
        return proxyConfig.getMonolithUrl();
    }

    private Mono<ServerResponse> forwardRequest(ServerRequest request, String targetUrl, String path) {
        String queryString = request.uri().getQuery();
        String fullUrl = targetUrl + path;
        if (queryString != null && !queryString.isEmpty()) {
            fullUrl += "?" + queryString;
        }

        HttpMethod method = request.method();

        WebClient.RequestBodySpec bodySpec = webClient.method(method)
                .uri(fullUrl);

        request.headers().asHttpHeaders().forEach((name, values) -> {
            if (!name.equalsIgnoreCase("host")) {
                bodySpec.header(name, values.toArray(new String[0]));
            }
        });

        Mono<ServerResponse> responseMono;

        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            Mono<String> body = request.bodyToMono(String.class);
            responseMono = body.flatMap(requestBody ->
                    bodySpec.bodyValue(requestBody)
                            .exchangeToMono(this::buildServerResponse)
            );
        } else {
            responseMono = bodySpec.exchangeToMono(this::buildServerResponse);
        }

        return responseMono.onErrorResume(e ->
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("{\"error\": \"Upstream service error: " + e.getMessage() + "\"}")
        );
    }

    private Mono<ServerResponse> buildServerResponse(org.springframework.web.reactive.function.client.ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    ServerResponse.BodyBuilder builder = ServerResponse.status(response.statusCode());
                    response.headers().asHttpHeaders().forEach((name, values) -> {
                        if (!name.equalsIgnoreCase("transfer-encoding") && !name.equalsIgnoreCase("content-length")) {
                            builder.header(name, values.toArray(new String[0]));
                        }
                    });
                    return builder.bodyValue(body);
                });
    }
}
