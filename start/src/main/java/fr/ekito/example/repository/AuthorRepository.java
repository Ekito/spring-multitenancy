package fr.ekito.example.repository;

import fr.ekito.example.domain.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Author entity.
 */
public interface AuthorRepository extends MongoRepository<Author, String> {

}
