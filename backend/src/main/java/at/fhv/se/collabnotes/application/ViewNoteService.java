package at.fhv.se.collabnotes.application;

import java.util.List;
import java.util.Optional;

import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.NoteTitleDTO;

public interface ViewNoteService {
    public List<NoteTitleDTO> allNoteTitles();

	public Optional<NoteDTO> viewNote(String noteId);

    public List<NoteDTO> allNotes();
}
