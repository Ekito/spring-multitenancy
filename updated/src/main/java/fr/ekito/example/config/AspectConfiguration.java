package fr.ekito.example.config;

import fr.ekito.example.aop.logging.LoggingAspect;
import fr.ekito.example.aop.multitenant.MultitenantRepositoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {

    @Bean
    @Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean
    public MultitenantRepositoryAspect multitenantRepositoryAspect() {
        return new MultitenantRepositoryAspect();
    }
}
