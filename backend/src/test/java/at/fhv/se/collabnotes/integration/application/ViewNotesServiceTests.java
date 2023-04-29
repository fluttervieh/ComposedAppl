package at.fhv.se.collabnotes.integration.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.NoteTitleDTO;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;
import at.fhv.se.collabnotes.utils.TestingUtils;

@SpringBootTest
public class ViewNotesServiceTests {

    @Autowired
    private ViewNoteService viewNoteService;

    @MockBean
    private NoteRepository noteRepository;

    @MockBean
    private StatisticsRepository statsRepo;
    
    @Test
    void allNoteTitles_when_empty_repository_empty_titles() {
        // given
        Mockito.when(noteRepository.findAllNotes()).thenReturn(Collections.emptyList());

        // when
        List<NoteTitleDTO> noteTitles = viewNoteService.allNoteTitles();

        // then
        assertTrue(noteTitles.isEmpty());
    }

    @Test
    void allNoteTitles_when_3notesinrepository_3titles() {
        // given
        List<String> noteTitlesExpected = Arrays.asList("Todo 1", "Todo 2", "Todo 3");
        
        List<Note> notes = noteTitlesExpected.stream()
            .map(title -> Note.create(title, new NoteId(java.util.UUID.randomUUID().toString().toUpperCase())))
            .collect(Collectors.toList());

         Mockito.when(noteRepository.findAllNotes()).thenReturn(notes);
            
        // when
        List<NoteTitleDTO> noteTitlesActual = viewNoteService.allNoteTitles();

        // then
        TestingUtils.assertEqualsCollections(notes, noteTitlesActual, (note, titleDto) -> {
            assertEquals(note.noteId().id(), titleDto.getId());
            assertEquals(note.title(), titleDto.getTitle());
        });
    }

    @Test
    void test_viewNote_not_exists() {
        // given
        NoteId noteId = new NoteId("42");
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.empty());

        // when
        Optional<NoteDTO> noteDto = this.viewNoteService.viewNote(noteId.id());

        // then
        assertEquals(Optional.empty(), noteDto);
    }

    @Test
    void test_viewNote_noitems() {
        // given
        NoteId noteId = new NoteId("42");
        Note note = Note.create("TODO", noteId);
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when
        NoteDTO noteDto = this.viewNoteService.viewNote(noteId.id()).get();

        // then
        assertEquals(note.noteId().id(), noteDto.getId());
        assertEquals(note.version(), noteDto.getVersion());
        assertEquals(note.title(), noteDto.getTitle());
    }

    @Test
    void test_viewNote_3items() {
        // given
        NoteId noteId = new NoteId("42");
        Note note = Note.create("TODO", noteId);
        note.addItem(new NoteItem(noteId, "Item 1"));
        note.addItem(new NoteItem(noteId, "Item 2"));
        note.addItem(new NoteItem(noteId, "Item 3"));
        
        Mockito.when(noteRepository.noteById(noteId)).thenReturn(Optional.of(note));

        // when
        NoteDTO noteDto = this.viewNoteService.viewNote(noteId.id()).get();

        // then
        assertEquals(note.noteId().id(), noteDto.getId());
        assertEquals(note.version(), noteDto.getVersion());
        assertEquals(note.title(), noteDto.getTitle());

        assertEquals(note.items().size(), noteDto.getItems().size());
        for (NoteItem i : note.items()) {
            assertTrue(noteDto.getItems().contains(i.text()));
        }
    }
}
