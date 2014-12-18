package fr.ekito.example.repository;

import fr.ekito.example.domain.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Book entity.
 */
public interface BookRepository extends MongoRepository<Book, String> {

}
