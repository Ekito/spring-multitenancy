package fr.ekito.example.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.ekito.example.domain.Author;
import fr.ekito.example.repository.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Author.
 */
@RestController
@RequestMapping("/app")
public class AuthorResource {

    private final Logger log = LoggerFactory.getLogger(AuthorResource.class);

    @Inject
    private AuthorRepository authorRepository;

    /**
     * POST  /rest/authors -> Create a new author.
     */
    @RequestMapping(value = "/rest/authors",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Author author) {
        log.debug("REST request to save Author : {}", author);
        authorRepository.save(author);
    }

    /**
     * GET  /rest/authors -> get all the authors.
     */
    @RequestMapping(value = "/rest/authors",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Author> getAll() {
        log.debug("REST request to get all Authors");
        return authorRepository.findAll();
    }

    /**
     * GET  /rest/authors/:id -> get the "id" author.
     */
    @RequestMapping(value = "/rest/authors/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Author> get(@PathVariable String id) {
        log.debug("REST request to get Author : {}", id);
        return Optional.ofNullable(authorRepository.findOne(id))
            .map(author -> new ResponseEntity<>(
                author,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/authors/:id -> delete the "id" author.
     */
    @RequestMapping(value = "/rest/authors/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete Author : {}", id);
        authorRepository.delete(id);
    }
}
