package fr.ekito.example.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.ekito.example.domain.Book;
import fr.ekito.example.repository.BookRepository;
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
 * REST controller for managing Book.
 */
@RestController
@RequestMapping("/app")
public class BookResource {

    private final Logger log = LoggerFactory.getLogger(BookResource.class);

    @Inject
    private BookRepository bookRepository;

    /**
     * POST  /rest/books -> Create a new book.
     */
    @RequestMapping(value = "/rest/books",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Book book) {
        log.debug("REST request to save Book : {}", book);
        bookRepository.save(book);
    }

    /**
     * GET  /rest/books -> get all the books.
     */
    @RequestMapping(value = "/rest/books",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Book> getAll() {
        log.debug("REST request to get all Books");
        return bookRepository.findAll();
    }

    /**
     * GET  /rest/books/:id -> get the "id" book.
     */
    @RequestMapping(value = "/rest/books/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Book> get(@PathVariable String id) {
        log.debug("REST request to get Book : {}", id);
        return Optional.ofNullable(bookRepository.findOne(id))
            .map(book -> new ResponseEntity<>(
                book,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/books/:id -> delete the "id" book.
     */
    @RequestMapping(value = "/rest/books/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete Book : {}", id);
        bookRepository.delete(id);
    }
}
