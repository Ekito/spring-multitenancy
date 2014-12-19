package fr.ekito.example.security;

import fr.ekito.example.domain.Domain;
import fr.ekito.example.domain.User;
import fr.ekito.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);


    public static UserRepository userRepository;


    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        String userName = null;

        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }

        return userName;
    }

    public static Domain getCurrentDomain() {
        Domain domain = null;
        String currentLogin = getCurrentLogin();
        if (currentLogin != null) {
            if (userRepository != null) {
                User user = userRepository.findOne(currentLogin);
                domain = user.getUserDomain();
                logger.info("get current domain : {} - {}", currentLogin, domain);
            } else {
                logger.warn("couldn't get user domain - userRepository is null");
            }
        }
        return domain;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        final Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }

        return true;
    }
}
