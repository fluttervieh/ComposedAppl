package at.fhv.se.collabnotes.application.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.domain.events.DomainEventSubscriber;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;
import at.fhv.se.collabnotes.domain.events.NoteCreated;
import at.fhv.se.collabnotes.domain.events.NoteDeleted;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.infrastructure.HibernateEventRepository;

public class CreateNoteServiceImpl implements CreateNoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private HibernateEventRepository eventRepo;

    @Transactional
    @Override
    public NoteId createNote(String title) {
        NoteId noteId = this.noteRepository.nextIdentity();
        Note note = Note.create(title, noteId);

        this.eventRepo.newEvent(NoteCreated.fromNote(note));
        this.noteRepository.add(note);

        return noteId;
    }

    @Transactional
    @Override
    public void addItem(String noteIdStr, int version, String itemText) throws VersionMismatchException {
        NoteId noteId = new NoteId(noteIdStr);
        Optional<Note> noteOpt = this.noteRepository.noteById(noteId);

        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.matchVersion(version);

            note.subscribe(new DomainEventSubscriber<ItemAdded>() {
                @Override
                public void handleEvent(ItemAdded event) {
                    eventRepo.newEvent(event);
                }
                @Override
                public Class<ItemAdded> subscribedToEventType() {
                    return ItemAdded.class;
                }
            });

            note.addItem(new NoteItem(noteId, itemText));
        }
    }

    @Transactional
    @Override
    public void removeItem(String noteIdStr, int version, String itemText) throws VersionMismatchException {
        NoteId noteId = new NoteId(noteIdStr);
        Optional<Note> noteOpt = this.noteRepository.noteById(noteId);

        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.matchVersion(version);

            NoteItem itemToRemove = new NoteItem(noteId, itemText);

            for (NoteItem item : note.items()) {
                if (item.equals(itemToRemove)) {
                    note.subscribe(new DomainEventSubscriber<ItemRemoved>() {
                        @Override
                        public void handleEvent(ItemRemoved event) {
                            eventRepo.newEvent(event);
                        }
                        @Override
                        public Class<ItemRemoved> subscribedToEventType() {
                            return ItemRemoved.class;
                        }
                    });

                    note.removeItem(item);
 
                    break;
                }
            }
        }
    }

    @Transactional
    @Override
    public void deleteNote(String noteIdStr, int version) throws VersionMismatchException {
        NoteId noteId = new NoteId(noteIdStr);
        Optional<Note> noteOpt = this.noteRepository.noteById(noteId);

        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.matchVersion(version);

            this.eventRepo.newEvent(NoteDeleted.fromNote(note));
            this.noteRepository.delete(note);
        }
    }
}
