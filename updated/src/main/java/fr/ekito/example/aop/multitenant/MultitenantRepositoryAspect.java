package fr.ekito.example.aop.multitenant;

import fr.ekito.example.domain.MultitenantEntity;
import fr.ekito.example.domain.User;
import fr.ekito.example.repository.UserRepository;
import fr.ekito.example.security.SecurityUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Aspect for logging execution of service and repository Spring components.
 */
@Aspect
public class MultitenantRepositoryAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Environment env;

    @Autowired
    UserRepository userRepository;

    @Before("bean(*Repository) && execution(* *..save(*)) && args(entity)")
    public void checkMultitenantEntity(MultitenantEntity entity) throws Throwable {
        log.info("@ checkMultitenantEntity {}", entity);
        if (entity.getUserDomain() == null) {
            log.warn("@ checkMultitenantEntity - no group found");
            String login = SecurityUtils.getCurrentLogin();
            User user = userRepository.findOne(login);
            entity.setUserDomain(user.getUserDomain());
            log.warn("@ assign group from {}",user);
        } else {
            log.debug("@ group is already defined before save operation");
        }
    }
}
