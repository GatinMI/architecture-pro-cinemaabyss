package com.cinemaabyss.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {

    @Value("${MONOLITH_URL}")
    private String monolithUrl;

    @Value("${MOVIES_SERVICE_URL}")
    private String moviesServiceUrl;

    @Value("${EVENTS_SERVICE_URL}")
    private String eventsServiceUrl;

    @Value("${GRADUAL_MIGRATION}")
    private boolean gradualMigration;

    @Value("${MOVIES_MIGRATION_PERCENT}")
    private int moviesMigrationPercent;

    public String getMonolithUrl() {
        return monolithUrl;
    }

    public String getMoviesServiceUrl() {
        return moviesServiceUrl;
    }

    public String getEventsServiceUrl() {
        return eventsServiceUrl;
    }

    public boolean isGradualMigration() {
        return gradualMigration;
    }

    public int getMoviesMigrationPercent() {
        return moviesMigrationPercent;
    }
}
