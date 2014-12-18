package fr.ekito.example.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import fr.ekito.example.Application;
import fr.ekito.example.domain.Book;
import fr.ekito.example.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BookResource REST controller.
 *
 * @see BookResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class BookResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";
    
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    
    private static final LocalDate DEFAULT_PUBLICATION_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_PUBLICATION_DATE = new LocalDate();
    
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_PRICE = BigDecimal.ONE;
    

    @Inject
    private BookRepository bookRepository;

    private MockMvc restBookMockMvc;

    private Book book;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BookResource bookResource = new BookResource();
        ReflectionTestUtils.setField(bookResource, "bookRepository", bookRepository);
        this.restBookMockMvc = MockMvcBuilders.standaloneSetup(bookResource).build();
    }

    @Before
    public void initTest() {
        bookRepository.deleteAll();
        book = new Book();
        book.setTitle(DEFAULT_TITLE);
        book.setDescription(DEFAULT_DESCRIPTION);
        book.setPublicationDate(DEFAULT_PUBLICATION_DATE);
        book.setPrice(DEFAULT_PRICE);
    }

    @Test
    public void createBook() throws Exception {
        // Validate the database is empty
        assertThat(bookRepository.findAll()).hasSize(0);

        // Create the Book
        restBookMockMvc.perform(post("/app/rest/books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(book)))
                .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(1);
        Book testBook = books.iterator().next();
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBook.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testBook.getPrice()).isEqualTo(DEFAULT_PRICE);
    }

    @Test
    public void getAllBooks() throws Exception {
        // Initialize the database
        bookRepository.save(book);

        // Get all the books
        restBookMockMvc.perform(get("/app/rest/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(book.getId()))
                .andExpect(jsonPath("$.[0].title").value(DEFAULT_TITLE.toString()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.[0].publicationDate").value(DEFAULT_PUBLICATION_DATE.toString()))
                .andExpect(jsonPath("$.[0].price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    public void getBook() throws Exception {
        // Initialize the database
        bookRepository.save(book);

        // Get the book
        restBookMockMvc.perform(get("/app/rest/books/{id}", book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(book.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.publicationDate").value(DEFAULT_PUBLICATION_DATE.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()));
    }

    @Test
    public void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get("/app/rest/books/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBook() throws Exception {
        // Initialize the database
        bookRepository.save(book);

        // Update the book
        book.setTitle(UPDATED_TITLE);
        book.setDescription(UPDATED_DESCRIPTION);
        book.setPublicationDate(UPDATED_PUBLICATION_DATE);
        book.setPrice(UPDATED_PRICE);
        restBookMockMvc.perform(post("/app/rest/books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(book)))
                .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(1);
        Book testBook = books.iterator().next();
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBook.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testBook.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    public void deleteBook() throws Exception {
        // Initialize the database
        bookRepository.save(book);

        // Get the book
        restBookMockMvc.perform(delete("/app/rest/books/{id}", book.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(0);
    }
}
