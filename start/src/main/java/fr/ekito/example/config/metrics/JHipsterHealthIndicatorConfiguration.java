package fr.ekito.example.config.metrics;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.inject.Inject;

@Configuration
public class JHipsterHealthIndicatorConfiguration {

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Bean
    public HealthIndicator mailHealthIndicator() {
        return new JavaMailHealthIndicator(javaMailSender);
    }
}
