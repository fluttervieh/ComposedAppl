package at.fhv.se.collabnotes.integration.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;
import at.fhv.se.collabnotes.infrastructure.HibernateNoteRepository;

@SpringBootTest
@Transactional
public class HibernateNoteRepositoryTests {

    @Autowired
    private HibernateNoteRepository noteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @MockBean
    private StatisticsRepository statsRepo;

    @Test
    void given_note_when_addnotetorepository_then_returnequalsnote() {
        // given
        NoteId idExpected = new NoteId("42");
        Note noteExpected = Note.create("TODO", idExpected);

        // when
        this.noteRepository.add(noteExpected);
        this.flushSession();

        // (re) load it from the DB
        Note noteActual = this.noteRepository.noteById(idExpected).get();

        // then
        assertEquals(noteExpected, noteActual);
        assertEquals(noteExpected.noteId(), noteActual.noteId());
    }

    @Test
    void given_emptyrespository_when_fetchallnotes_then_returnemptynotes() {
        assertTrue(this.noteRepository.findAllNotes().isEmpty());
    }

    @Test
    void given_3notesinrepository_when_fetchallnotes_then_returnequalnotes() {
        // given
        List<String> noteTitlesExpected = Arrays.asList("Todo 1", "Todo 2", "Todo 3");
        List<Note> notesExpected = noteTitlesExpected.stream()
            .map(title -> Note.create(title, this.noteRepository.nextIdentity()))
            .collect(Collectors.toList());

        // when
        notesExpected.forEach(note -> {
            this.noteRepository.add(note);
        });
        this.flushSession();
        List<Note> notesActual = this.noteRepository.findAllNotes();

        // then
        assertEquals(noteTitlesExpected.size(), notesActual.size());
        for (Note n : notesActual) {
            assertTrue(notesExpected.contains(n));
        }
    }

    @Test
    void given_note_add_items_returned() {
        // given
        NoteId noteId = this.noteRepository.nextIdentity();
        Note note = Note.create("TODO", noteId);

        NoteItem i1 = new NoteItem(noteId, "Item 1");
        NoteItem i2 = new NoteItem(noteId, "Item 2");
        NoteItem i3 = new NoteItem(noteId, "Item 3");

        note.addItem(i1);
        note.addItem(i2);
        note.addItem(i3);

        // when
        this.noteRepository.add(note);
        this.flushSession();
        // (re) load it from the DB
        Note noteActual = this.noteRepository.noteById(noteId).get();

        // then
        assertEquals(note, noteActual);
        assertEquals(note.noteId(), noteActual.noteId());
        assertEquals(note.items(), noteActual.items());
    }

    @Test
    void given_note_add_item_returned() {
        // given
        NoteId noteId = this.noteRepository.nextIdentity();
        Note note = Note.create("TODO", noteId);
        this.noteRepository.add(note);
        this.flushSession();

        // when
        Note noteExpected = this.noteRepository.noteById(noteId).get();
        noteExpected.addItem(new NoteItem(noteId, "Item 1"));
        this.flushSession();
        Note noteActual = this.noteRepository.noteById(noteId).get();

        // then
        assertEquals(noteExpected, noteActual);
        assertEquals(noteExpected.noteId(), noteActual.noteId());
        assertEquals(noteExpected.items(), noteActual.items());
    }

    @Test
    void given_note_remove_not_returned_from_DB() {
        // given
        NoteId noteId = this.noteRepository.nextIdentity();
        Note note = Note.create("TODO", noteId);
        NoteItem i1 = new NoteItem(noteId, "Item 1");
        NoteItem i2 = new NoteItem(noteId, "Item 2");
        NoteItem i3 = new NoteItem(noteId, "Item 3");

        note.addItem(i1);
        note.addItem(i2);
        note.addItem(i3);

        this.noteRepository.add(note);
        
        // when
        //Note noteActual = this.noteRepository.noteById(noteId).get();
        this.noteRepository.delete(note);
        this.flushSession();
        
        Optional<Note> noteDeleted = this.noteRepository.noteById(noteId);

        // then
        assertSame(Optional.empty(), noteDeleted);
    }

    @Test
    void given_note_changed_updated_version() {
        // given
        NoteId noteId = this.noteRepository.nextIdentity();
        Note note = Note.create("TODO", noteId);
        this.noteRepository.add(note);
        this.flushSession();
        int initVersion = note.version();

        // when
        //note = this.noteRepository.noteById(noteId).get();
        NoteItem item = new NoteItem(noteId, "Item 1");
        note.addItem(item);
        this.flushSession();

        // then
        assertNotEquals(initVersion, note.version());
    }

    private void flushSession() {
        entityManager.flush();
        //this.sessionFactory.getCurrentSession().flush();
    }
}
