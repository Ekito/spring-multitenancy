package fr.ekito.example.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.ekito.example.domain.Domain;
import fr.ekito.example.repository.DomainRepository;
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
 * REST controller for managing Domain.
 */
@RestController
@RequestMapping("/app")
public class DomainResource {

    private final Logger log = LoggerFactory.getLogger(DomainResource.class);

    @Inject
    private DomainRepository domainRepository;

    /**
     * POST  /rest/domains -> Create a new domain.
     */
    @RequestMapping(value = "/rest/domains",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Domain domain) {
        log.debug("REST request to save Domain : {}", domain);
        domainRepository.save(domain);
    }

    /**
     * GET  /rest/domains -> get all the domains.
     */
    @RequestMapping(value = "/rest/domains",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Domain> getAll() {
        log.debug("REST request to get all Domains");
        return domainRepository.findAll();
    }

    /**
     * GET  /rest/domains/:id -> get the "id" domain.
     */
    @RequestMapping(value = "/rest/domains/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Domain> get(@PathVariable String id) {
        log.debug("REST request to get Domain : {}", id);
        return Optional.ofNullable(domainRepository.findOne(id))
            .map(domain -> new ResponseEntity<>(
                domain,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/domains/:id -> delete the "id" domain.
     */
    @RequestMapping(value = "/rest/domains/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete Domain : {}", id);
        domainRepository.delete(id);
    }
}
