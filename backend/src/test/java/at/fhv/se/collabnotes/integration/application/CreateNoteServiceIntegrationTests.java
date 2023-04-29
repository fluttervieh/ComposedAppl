package at.fhv.se.collabnotes.integration.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

@SpringBootTest
@Transactional
public class CreateNoteServiceIntegrationTests {
    
    @Autowired
    private CreateNoteService createNoteService;

    @Autowired
    private NoteRepository noteRepository;

    @MockBean
    private StatisticsRepository statsRepo;
    
    @Test
    void test_createNote() {
        // given
        String noteTitle = "TODO";
 
        // when
        NoteId noteId = this.createNoteService.createNote(noteTitle);
        Note note = this.noteRepository.noteById(noteId).get();

        // Then
        assertEquals(noteTitle, note.title());
        assertEquals(noteId, note.noteId());
    }
}
