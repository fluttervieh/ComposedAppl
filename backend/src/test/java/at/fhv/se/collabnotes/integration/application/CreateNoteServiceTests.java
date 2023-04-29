package at.fhv.se.collabnotes.integration.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;
import at.fhv.se.collabnotes.domain.events.NoteCreated;
import at.fhv.se.collabnotes.domain.events.NoteDeleted;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;
import at.fhv.se.collabnotes.infrastructure.HibernateEventRepository;

@SpringBootTest
public class CreateNoteServiceTests {
    
    @Autowired
    private CreateNoteService createNoteService;
    
    @MockBean
    private NoteRepository noteRepository;

    @MockBean
    private StatisticsRepository statsRepo;
    
    @MockBean
    private HibernateEventRepository eventRepo;

    @Test
    void test_createNote() {
        // given
        String noteTitle = "TODO";

        InOrder inOrder = Mockito.inOrder(this.noteRepository);
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        
        // when
        NoteId noteId = this.createNoteService.createNote(noteTitle);

        // Then
        inOrder.verify(this.noteRepository).nextIdentity();
        inOrder.verify(this.noteRepository).add(any());

        verify(this.noteRepository, times(1)).add(captor.capture());

        Note note = captor.getValue();

        assertEquals(noteTitle, note.title());
        assertSame(noteId, note.noteId());
    }

    @Test
    void test_addItem() throws VersionMismatchException {
        // given
        String noteIdStr = "42";
        String itemText = "Item 1";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
    
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when
        this.createNoteService.addItem(noteIdStr, note.version(), itemText);

        // then
        assertEquals(1, note.items().size());
        NoteItem item = note.items().iterator().next();
        assertEquals(itemText, item.text());
    }

    @Test
    void test_addItem_mismatch_version() {
        // given
        String noteIdStr = "42";
        String itemText = "Item 1";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
        int noteVersion = 1;
        
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when ... then
        assertThrows(
            VersionMismatchException.class, 
            () -> this.createNoteService.addItem(noteIdStr, noteVersion, itemText));
    }

    @Test
    void test_removeItem() throws VersionMismatchException {
        // given
        String noteIdStr = "42";
        String itemText = "Item 1";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
        NoteItem noteItem = new NoteItem(noteId, itemText);
        note.addItem(noteItem);

        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when
        this.createNoteService.removeItem(noteIdStr, note.version(), itemText);

        // then
        assertTrue(note.items().isEmpty());
    }

    @Test
    void test_removeItem_mismatch_version() {
        // given
        String noteIdStr = "42";
        String itemText = "Item 1";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
        NoteItem noteItem = new NoteItem(noteId, itemText);
        note.addItem(noteItem);
        int noteVersion = 1;

        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when ... then
        assertThrows(
            VersionMismatchException.class, 
            () -> this.createNoteService.removeItem(noteIdStr, noteVersion, itemText));
    }

    @Test
    void test_delete_note() throws VersionMismatchException {
        // given
        String noteIdStr = "42";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);

        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when
        this.createNoteService.deleteNote(noteIdStr, note.version());

        // then
        Mockito.verify(noteRepository, times(1)).delete(note);
    }

    @Test
    void test_delete_note_mismatch_version() {
        // given
        String noteIdStr = "42";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
        int noteVersion = 1;

        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when ... then
        assertThrows(
            VersionMismatchException.class, 
            () -> this.createNoteService.deleteNote(noteIdStr, noteVersion));
    }

    @Test
    void given_notetitleandservice_andpublisher_when_newnote_then_addedeventtorepo() {
        // given
        String noteTitle = "TODO";
        ArgumentCaptor<NoteCreated> captor = ArgumentCaptor.forClass(NoteCreated.class);

        // when
        NoteId noteId = this.createNoteService.createNote(noteTitle);

        // then
        Mockito.verify(eventRepo, times(1)).newEvent(captor.capture());
        NoteCreated event = captor.getValue();

        assertEquals(noteId, event.noteId());
        assertEquals(noteTitle, event.noteTitle());
    }

    @Test
    void given_noteideandservice_andpublisher_when_deletenote_then_addeventtorepo() throws VersionMismatchException {
        // given
        NoteId noteId = new NoteId("42");
        Note note = Note.create("TODO", noteId);
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));
        ArgumentCaptor<NoteDeleted> captor = ArgumentCaptor.forClass(NoteDeleted.class);

        // when
        this.createNoteService.deleteNote(noteId.id(), 0);

        // then
        Mockito.verify(eventRepo, times(1)).newEvent(captor.capture());
        NoteDeleted event = captor.getValue();

        assertEquals(noteId, event.noteId());
    }

    @Test
    void given_noteideandservice_andpublisher_when_addItem_then_addeventtorepo() throws VersionMismatchException {
        // given
        String noteIdStr = "42";
        String itemText = "Item 1";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));
        ArgumentCaptor<ItemAdded> captor = ArgumentCaptor.forClass(ItemAdded.class);

        // when
        this.createNoteService.addItem(noteIdStr, note.version(), itemText);

        // then
        Mockito.verify(eventRepo, times(1)).newEvent(captor.capture());
        ItemAdded event = captor.getValue();

        assertEquals(noteId, event.noteId());
        assertEquals(itemText, event.itemText());
    }

    @Test
    void given_noteideandservice_andpublisher_when_removeItem_then_addeventtorepo() throws VersionMismatchException {
        // given
        String noteIdStr = "42";
        String itemText = "Item 1";
        NoteId noteId = new NoteId(noteIdStr);
        Note note = Note.create("TODO", noteId);
        NoteItem noteItem = new NoteItem(noteId, itemText);
        note.addItem(noteItem);
        ArgumentCaptor<ItemRemoved> captor = ArgumentCaptor.forClass(ItemRemoved.class);

        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when
        this.createNoteService.removeItem(noteIdStr, note.version(), itemText);

        // then
        Mockito.verify(eventRepo, times(1)).newEvent(captor.capture());
        ItemRemoved event = captor.getValue();

        assertEquals(noteId, event.noteId());
        assertEquals(itemText, event.itemText());
    }
}
