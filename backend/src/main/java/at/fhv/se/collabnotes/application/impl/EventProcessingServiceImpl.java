package at.fhv.se.collabnotes.application.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import at.fhv.se.collabnotes.application.EventProcessingService;
import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;
import at.fhv.se.collabnotes.domain.events.NoteCreated;
import at.fhv.se.collabnotes.domain.events.NoteDeleted;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.infrastructure.HibernateEventRepository;
import at.fhv.se.collabnotes.infrastructure.PersistedEvent;

public class EventProcessingServiceImpl implements EventProcessingService {

    @Autowired
    private StatisticsService statsService;

    @Autowired
    private HibernateEventRepository eventRepo;

    @Transactional
    @Override
    public void processNextEvent() {
        Optional<PersistedEvent> pEvtOopt = eventRepo.nextEvent();
        pEvtOopt.ifPresent(persistedEvent -> {
            if (persistedEvent.type().equals(NoteCreated.class.getSimpleName())) {
                Optional<NoteCreated> dEvtOpt = persistedEvent.domainEventOf(NoteCreated.class);
                dEvtOpt.ifPresent(evt -> statsService.newNote(evt.noteId()));
            
            } else if (persistedEvent.type().equals(NoteDeleted.class.getSimpleName())) {
                Optional<NoteDeleted> dEvtOpt = persistedEvent.domainEventOf(NoteDeleted.class);
                dEvtOpt.ifPresent(evt -> statsService.removeNote(evt.noteId(), evt.itemCount()));

            } else if (persistedEvent.type().equals(ItemAdded.class.getSimpleName())) {
                Optional<ItemAdded> dEvtOpt = persistedEvent.domainEventOf(ItemAdded.class);
                dEvtOpt.ifPresent(evt -> statsService.newItem(new NoteItem(evt.noteId(), evt.itemText())));

            } else if (persistedEvent.type().equals(ItemRemoved.class.getSimpleName())) {
                Optional<ItemRemoved> dEvtOpt = persistedEvent.domainEventOf(ItemRemoved.class);
                dEvtOpt.ifPresent(evt -> statsService.removeItem(new NoteItem(evt.noteId(), evt.itemText())));
            }

            persistedEvent.consume();
        });
    }
}
