package at.fhv.se.collabnotes.integration.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.domain.events.DomainEvent;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;
import at.fhv.se.collabnotes.domain.events.NoteCreated;
import at.fhv.se.collabnotes.domain.events.NoteDeleted;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.infrastructure.HibernateEventRepository;
import at.fhv.se.collabnotes.infrastructure.PersistedEvent;

@SpringBootTest
@Transactional
public class HibernateEventRepositoryTests {

    @Autowired
    private HibernateEventRepository eventRepo;

    @PersistenceContext
    private EntityManager em;

    @Test
    void given_emptyeventtable_when_nextevent_then_returnempty() {
        // given ... when
        Optional<PersistedEvent> event = eventRepo.nextEvent();

        // then
        assertSame(Optional.empty(), event);
    }

    @Test
    void given_NoteCreatedeventintable_when_nextevent_then_returnevent() {
        // given
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new NoteCreated(noteId, "TODO");
        PersistedEvent persistedEvent = eventRepo.newEvent(domainEvent);
        em.flush();

        // when
        Optional<PersistedEvent> nextEventOpt = eventRepo.nextEvent();

        // then
        PersistedEvent nextEvent = nextEventOpt.get();

        assertEquals(persistedEvent.id(), nextEvent.id());
        assertEquals(persistedEvent.created(), nextEvent.created());
        assertEquals(persistedEvent.isConsumed(), nextEvent.isConsumed());
        assertFalse(nextEvent.isConsumed());

        Optional<NoteCreated> noteCreated = nextEvent.domainEventOf(NoteCreated.class);

        assertEquals(domainEvent, noteCreated.get());
    }

    @Test
    void given_NoteDeletedeventintable_when_nextevent_then_returnevent() {
        // given
        NoteId noteId = new NoteId("42");
        int itemCount = 42;
        DomainEvent domainEvent = new NoteDeleted(noteId, itemCount);
        PersistedEvent persistedEvent = eventRepo.newEvent(domainEvent);
        em.flush();

        // when
        Optional<PersistedEvent> nextEventOpt = eventRepo.nextEvent();

        // then
        PersistedEvent nextEvent = nextEventOpt.get();

        assertEquals(persistedEvent.id(), nextEvent.id());
        assertEquals(persistedEvent.created(), nextEvent.created());
        assertEquals(persistedEvent.isConsumed(), nextEvent.isConsumed());
        assertFalse(nextEvent.isConsumed());

        Optional<NoteDeleted> noteCreated = nextEvent.domainEventOf(NoteDeleted.class);

        assertEquals(domainEvent, noteCreated.get());
    }

    @Test
    void given_ItemAddedeventintable_when_nextevent_then_returnevent() {
        // given
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new ItemAdded(noteId, "Item 1");
        PersistedEvent persistedEvent = eventRepo.newEvent(domainEvent);
        em.flush();

        // when
        Optional<PersistedEvent> nextEventOpt = eventRepo.nextEvent();

        // then
        PersistedEvent nextEvent = nextEventOpt.get();

        assertEquals(persistedEvent.id(), nextEvent.id());
        assertEquals(persistedEvent.created(), nextEvent.created());
        assertEquals(persistedEvent.isConsumed(), nextEvent.isConsumed());
        assertFalse(nextEvent.isConsumed());

        Optional<ItemAdded> noteCreated = nextEvent.domainEventOf(ItemAdded.class);

        assertEquals(domainEvent, noteCreated.get());
    }

    @Test
    void given_ItemRemovedeventintable_when_nextevent_then_returnevent() {
        // given
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new ItemRemoved(noteId, "Item 1");
        PersistedEvent persistedEvent = eventRepo.newEvent(domainEvent);
        em.flush();

        // when
        Optional<PersistedEvent> nextEventOpt = eventRepo.nextEvent();

        // then
        PersistedEvent nextEvent = nextEventOpt.get();

        assertEquals(persistedEvent.id(), nextEvent.id());
        assertEquals(persistedEvent.created(), nextEvent.created());
        assertEquals(persistedEvent.isConsumed(), nextEvent.isConsumed());
        assertFalse(nextEvent.isConsumed());

        Optional<ItemRemoved> noteCreated = nextEvent.domainEventOf(ItemRemoved.class);

        assertEquals(domainEvent, noteCreated.get());
    }

    @Test
    void given_eventintable_when_nextevent_andconsumed_then_returnnoevent() {
        // given
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new NoteCreated(noteId, "TODO");
        eventRepo.newEvent(domainEvent);
        em.flush();

        // when
        Optional<PersistedEvent> nextEventOpt = eventRepo.nextEvent();
        PersistedEvent nextEvent = nextEventOpt.get();
        nextEvent.consume();
        em.flush();

        // then
        nextEventOpt = eventRepo.nextEvent();
        assertSame(Optional.empty(), nextEventOpt);
    }
}
