package at.fhv.se.collabnotes.domain.repositories;

import java.util.List;
import java.util.Optional;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;

public interface NoteRepository {

	public List<Note> findAllNotes();

	public NoteId nextIdentity();

	public void add(Note note);
	public void delete(Note note);

	public Optional<Note> noteById(NoteId id);
}
