package at.fhv.se.collabnotes.unit.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import at.fhv.se.collabnotes.application.dto.NoteDTO;

public class NoteDTOTests {
    
    @Test
    void given_builder_when_missing_id_then_throwsexception() {
        // given
        NoteDTO.Builder builder = NoteDTO.create();

        // when ... then
        assertThrows(RuntimeException.class, () -> builder.build());
    }

    @Test
    void given_builder_when_missing_title_then_throwsexception() {
        // given
        NoteDTO.Builder builder = NoteDTO.create().setId("42");

        // when ... then
        assertThrows(RuntimeException.class, () -> builder.build());
    }
}
