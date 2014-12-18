package fr.ekito.example.repository;

import fr.ekito.example.domain.Authority;
import fr.ekito.example.domain.Domain;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface DomainRepository extends MongoRepository<Domain, String> {
}
