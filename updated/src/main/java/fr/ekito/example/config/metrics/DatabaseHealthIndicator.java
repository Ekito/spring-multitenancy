package fr.ekito.example.config.metrics;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * SpringBoot Actuator HealthIndicator check for the Database.
 */
public class DatabaseHealthIndicator extends AbstractHealthIndicator {
    
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // do nothing
    }
}
