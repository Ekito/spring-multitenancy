package fr.ekito.example.service;

import fr.ekito.example.domain.Author;
import fr.ekito.example.domain.Authority;
import fr.ekito.example.domain.Domain;
import fr.ekito.example.domain.User;
import fr.ekito.example.repository.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Arnaud on 18/12/2014.
 */
@Service
public class DataBootstrap {


    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    UserService userService;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DomainRepository domainRepository;

    public void initData(){
        logger.info("init bootstrap ...");

        User admin =userRepository.findOne("admin");
        User user = userRepository.findOne("user");
        boolean userExists = admin != null && user != null;

        if (!userExists) {
            Authority role_admin = new Authority("ROLE_ADMIN");
            Authority role_user =  new Authority("ROLE_USER");

            role_admin = authorityRepository.save(role_admin);
            role_user = authorityRepository.save(role_user);

            Domain adminDomain = new Domain("admin_domain");
            Domain userDomain = new Domain("user_domain");
            domainRepository.save(adminDomain);
            domainRepository.save(userDomain);

            admin = userService.createUser("admin", "admin", "admin", "", "", "en", role_admin, adminDomain);
            admin.getAuthorities().add(role_user);
            userRepository.save(admin);

            user = userService.createUser("user", "user", "user", "", "", "en", role_user, userDomain);

            Author a1 = new Author();
            a1.setUserDomain(adminDomain);
            a1.setName("Victor Hugo");
            a1.setBirthDate(new LocalDate());
            authorRepository.save(a1);
        }
    }
}
