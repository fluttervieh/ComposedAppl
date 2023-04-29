package at.fhv.se.collabnotes.infrastructure;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import at.fhv.se.collabnotes.domain.model.Statistics;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

public class HibernateStatisticsRepository implements StatisticsRepository {
	// the ONE AND ONLY statistics entry is inserted through data.sql and has statsId = 1
	private static final int STATS_ID = 1;

    @PersistenceContext
    private EntityManager em;

	@Override
	public Statistics fetchStatistics() {
        TypedQuery<Statistics> query =  this.em.createQuery("from Statistics where statsId = :statsId", Statistics.class);
        query.setParameter("statsId", STATS_ID);
		return query.getSingleResult();
	}
}
