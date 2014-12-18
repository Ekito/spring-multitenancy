package fr.ekito.example.repository;

import fr.ekito.example.domain.PersistentAuditEvent;
import org.joda.time.LocalDateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Spring Data MongoDB repository for the PersistentAuditEvent entity.
 */
 public interface PersistenceAuditEventRepository extends MongoRepository<PersistentAuditEvent, String> {

    List<PersistentAuditEvent> findByPrincipal(String principal);

    List<PersistentAuditEvent> findByPrincipalAndAuditEventDateGreaterThan(String principal, LocalDateTime after);
    
    @Query("{auditEventDate: {$gt: ?0, $lte: ?1}}")
    List<PersistentAuditEvent> findByDates(LocalDateTime fromDate, LocalDateTime toDate);
}
