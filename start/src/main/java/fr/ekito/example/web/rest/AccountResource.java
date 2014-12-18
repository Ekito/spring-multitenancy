package fr.ekito.example.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.ekito.example.domain.Authority;
import fr.ekito.example.domain.User;
import fr.ekito.example.repository.UserRepository;
import fr.ekito.example.security.SecurityUtils;
import fr.ekito.example.service.MailService;
import fr.ekito.example.service.UserService;
import fr.ekito.example.web.rest.dto.UserDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private ServletContext servletContext;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    /**
     * POST  /rest/register -> register the user.
     */
    @RequestMapping(value = "/rest/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request,
                                             HttpServletResponse response) {
        return Optional.ofNullable(userRepository.findOne(userDTO.getLogin()))
            .map(user -> new ResponseEntity<String>("login already in use", HttpStatus.BAD_REQUEST))
            .orElseGet(() -> {
                if (userRepository.findOneByEmail(userDTO.getEmail()) != null) {
                    return new ResponseEntity<String>("e-mail address already in use", HttpStatus.BAD_REQUEST);
                }
                User user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                        userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(),
                        userDTO.getLangKey());
                final Locale locale = Locale.forLanguageTag(user.getLangKey());
                String content = createHtmlContentFromTemplate(user, locale, request, response);
                mailService.sendActivationEmail(user.getEmail(), content, locale);
                return new ResponseEntity<>(HttpStatus.CREATED);});
    }
    /**
     * GET  /rest/activate -> activate the registered user.
     */
    @RequestMapping(value = "/rest/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        return Optional.ofNullable(userService.activateRegistration(key))
            .map(user -> new ResponseEntity<String>(
                    user.getLogin(),
                    HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * GET  /rest/authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/rest/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(
                new UserDTO(
                    user.getLogin(),
                    null,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getLangKey(),
                    user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList())),
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /rest/account -> update the current user information.
     */
    @RequestMapping(value = "/rest/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> saveAccount(@RequestBody UserDTO userDTO) {
        User userHavingThisEmail = userRepository.findOneByEmail(userDTO.getEmail());
        if (userHavingThisEmail != null && !userHavingThisEmail.getLogin().equals(SecurityUtils.getCurrentLogin())) {
            return new ResponseEntity<String>("e-mail address already in use", HttpStatus.BAD_REQUEST);
        }
        userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * POST  /rest/change_password -> changes the current user's password
     */
    @RequestMapping(value = "/rest/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (StringUtils.isEmpty(password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String createHtmlContentFromTemplate(final User user, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
                                 request.getServerName() +       // "myhost"
                                 ":" + request.getServerPort());
        IWebContext context = new SpringWebContext(request, response, servletContext,
                locale, variables, applicationContext);
        return templateEngine.process("activationEmail", context);
    }
}
