package at.fhv.se.collabnotes.infrastructure;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import at.fhv.se.collabnotes.domain.events.DomainEvent;

// NOTE: this Repository implements no interface from the Domain model because
// it belongs to infrastructure / Application to implement eventual consistency
public class HibernateEventRepository {

	@PersistenceContext
	private EntityManager em;

	public PersistedEvent newEvent(DomainEvent event) {
		System.out.println("newEvent: " + this.em);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		Date created = new Date();
		String type = event.getClass().getSimpleName();
		
		try {
			String payload = objectMapper.writeValueAsString(event);
			PersistedEvent pe = new PersistedEvent(created, type, payload);
			this.em.persist(pe);
			return pe;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Optional<PersistedEvent> nextEvent() {
		System.out.println("nextEvent: " + this.em);

		TypedQuery<PersistedEvent> query = this.em.createQuery("from PersistedEvent where consumed = :consumed order by created", PersistedEvent.class);
		query.setParameter("consumed", false);
		query.setMaxResults(1);

		List<PersistedEvent> result = query.getResultList();
		if (result.size() != 1) {
			return Optional.empty();
		}
		
		return Optional.of(result.get(0));
	}
}
