package at.fhv.se.collabnotes.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;

public class NoteRepositoryTests {

    private NoteRepository noteRepository;

    @BeforeEach
    void createRepository() {
        this.noteRepository = new NoteRepository() {
            private Map<NoteId, Note> notes = new HashMap<>();
            
            @Override
            public List<Note> findAllNotes() {
                return notes.values().stream().collect(Collectors.toList());
            }

            @Override
            public NoteId nextIdentity() {
                return new NoteId(UUID.randomUUID().toString().toUpperCase());
            }

            @Override
            public void add(Note note) {
                this.notes.put(note.noteId(), note);
            }

            @Override
            public Optional<Note> noteById(NoteId id) {
                return Optional.ofNullable(this.notes.get(id));
                //return this.notes.stream().filter(n -> n.noteId().equals(id)).findFirst().get();
            }

            @Override
            public void delete(Note note) {
                this.notes.remove(note.noteId());
            }
        };
    }

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
