package at.fhv.se.collabnotes.integration.application;

import static org.mockito.Mockito.times;

import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import at.fhv.se.collabnotes.application.EventProcessingService;
import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.domain.events.DomainEvent;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;
import at.fhv.se.collabnotes.domain.events.NoteCreated;
import at.fhv.se.collabnotes.domain.events.NoteDeleted;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.infrastructure.HibernateEventRepository;
import at.fhv.se.collabnotes.infrastructure.PersistedEvent;

@TestPropertySource(properties = "app.scheduling.enable=false")
@SpringBootTest
@Transactional
public class EventProcessingServiceTests {
    
    @Autowired
    private EventProcessingService eventProcService;

    @MockBean
    private HibernateEventRepository eventRepo;

    @MockBean
    private StatisticsService statsService;

    @Test
    void given_NoteCreatedeventinrepo_when_fetch_update() {
        // given
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new NoteCreated(noteId, "TODO");
        PersistedEvent event = new PersistedEvent(null, NoteCreated.class.getSimpleName(), "");
        PersistedEvent eventSpy = Mockito.spy(event);
        Mockito.doReturn(Optional.of(domainEvent)).when(eventSpy).domainEventOf(NoteCreated.class);
        Mockito.when(eventRepo.nextEvent()).thenReturn(Optional.of(eventSpy));

        // when
        eventProcService.processNextEvent();

        // then
        Mockito.verify(statsService, times(1)).newNote(noteId);
        Mockito.verify(eventSpy, times(1)).consume();
    }

    @Test
    void given_NoteDeletedeventinrepo_when_fetch_update() {
        // given
        NoteId noteId = new NoteId("42");
        int itemCount = 0;
        DomainEvent domainEvent = new NoteDeleted(noteId, itemCount);
        PersistedEvent event = new PersistedEvent(null, NoteDeleted.class.getSimpleName(), "");
        PersistedEvent eventSpy = Mockito.spy(event);
        Mockito.doReturn(Optional.of(domainEvent)).when(eventSpy).domainEventOf(NoteDeleted.class);
        Mockito.when(eventRepo.nextEvent()).thenReturn(Optional.of(eventSpy));

        // when
        eventProcService.processNextEvent();

        // then
        Mockito.verify(statsService, times(1)).removeNote(noteId, itemCount);
        Mockito.verify(eventSpy, times(1)).consume();
    }

    @Test
    void given_ItemAddededeventinrepo_when_fetch_update() {
        // given
        String noteText =  "Item 1";
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new ItemAdded(noteId, noteText);
        PersistedEvent event = new PersistedEvent(null, ItemAdded.class.getSimpleName(), "");
        PersistedEvent eventSpy = Mockito.spy(event);
        Mockito.doReturn(Optional.of(domainEvent)).when(eventSpy).domainEventOf(ItemAdded.class);
        Mockito.when(eventRepo.nextEvent()).thenReturn(Optional.of(eventSpy));

        // when
        eventProcService.processNextEvent();

        // then
        Mockito.verify(statsService, times(1)).newItem(new NoteItem(noteId, noteText));
        Mockito.verify(eventSpy, times(1)).consume();
    }

    @Test
    void given_ItemRemovededeventinrepo_when_fetch_update() {
        // given
        String noteText =  "Item 1";
        NoteId noteId = new NoteId("42");
        DomainEvent domainEvent = new ItemRemoved(noteId, noteText);
        PersistedEvent event = new PersistedEvent(null, ItemRemoved.class.getSimpleName(), "");
        PersistedEvent eventSpy = Mockito.spy(event);
        Mockito.doReturn(Optional.of(domainEvent)).when(eventSpy).domainEventOf(ItemRemoved.class);
        Mockito.when(eventRepo.nextEvent()).thenReturn(Optional.of(eventSpy));

        // when
        eventProcService.processNextEvent();

        // then
        Mockito.verify(statsService, times(1)).removeItem(new NoteItem(noteId, noteText));
        Mockito.verify(eventSpy, times(1)).consume();
    }
}
