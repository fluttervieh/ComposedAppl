package at.fhv.se.collabnotes.integration.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.domain.model.Statistics;
import at.fhv.se.collabnotes.infrastructure.HibernateStatisticsRepository;

@SpringBootTest
@Transactional
public class HibernateStatisticsRepositoryTest {
    
    @Autowired
    private HibernateStatisticsRepository statsRepo;

    @PersistenceContext
    private EntityManager em;

    @Test
    void given_emptyrepo_when_fetchstatistics_then_return_default_statistics() {
        Statistics stats = this.statsRepo.fetchStatistics();

        assertNotNull(stats);
        assertEquals(Statistics.empty(), stats);
    }

    @Test
    void given_emptyrepo_when_fetchstatistics_then_insert_andreturn_empty_() {
        // given 
        Statistics emptyStats = Statistics.empty();

        // when
        Statistics stats = this.statsRepo.fetchStatistics();
        this.em.flush();
        Statistics statsRefetch = this.statsRepo.fetchStatistics();

        // then
        assertEquals(emptyStats, stats);
        assertEquals(statsRefetch, stats);
    }

    @Test
    void given_emptyrepo_when_fetchstatistics_and_update_then_fetch_equals() {
        // given 
        Statistics expectedStats = Statistics.create(1, 1);
        Statistics stats = this.statsRepo.fetchStatistics();
        
        // when
        stats.incrementItems();
        stats.incrementNotes();
        this.em.flush();

        Statistics statsRefetch = this.statsRepo.fetchStatistics();

        // then
        assertEquals(statsRefetch, expectedStats);
    }
}
