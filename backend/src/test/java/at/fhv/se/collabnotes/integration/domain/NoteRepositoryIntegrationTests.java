package at.fhv.se.collabnotes.integration.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

@SpringBootTest
@Transactional
public class NoteRepositoryIntegrationTests {

    @Autowired
    private NoteRepository noteRepository;

    @MockBean
    private StatisticsRepository statsRepo;
    
    @Test
    void save_note_return_equals_note() {
        // given
        NoteId idExpected = new NoteId("42");
        Note noteExpected = Note.create("TODO", idExpected);

        // when
        this.noteRepository.add(noteExpected);
        Optional<Note> noteActual = this.noteRepository.noteById(idExpected);

        // then
        assertEquals(noteExpected, noteActual.get());
        assertEquals(noteExpected.noteId(), noteActual.get().noteId());
    }

    @Test
    void empty_repo_does_not_return_anything() {
        assertTrue(this.noteRepository.findAllNotes().isEmpty());
    }

    @Test
    void given_multiplenotes_when_adding_then_allreturn() {
        // given
        List<String> noteTitlesExpected = Arrays.asList("Todo 1", "Todo 2", "Todo 3");
        List<Note> notesExpected = noteTitlesExpected.stream()
            .map(title -> Note.create(title, this.noteRepository.nextIdentity()))
            .collect(Collectors.toList());

        // when
        notesExpected.forEach(this.noteRepository::add);
        List<Note> notesActual = this.noteRepository.findAllNotes();

        // then
        assertEquals(noteTitlesExpected.size(), notesActual.size());
        for (Note n : notesActual) {
            assertTrue(notesExpected.contains(n));
        }
    }
}
