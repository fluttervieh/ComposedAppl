package at.fhv.se.collabnotes.application;

import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;

public interface CreateNoteService {

	NoteId createNote(String noteTitle);

	void addItem(String noteId, int noteVersion, String itemText) throws VersionMismatchException;

	void removeItem(String noteId, int noteVersion, String itemText) throws VersionMismatchException;

	void deleteNote(String noteId, int noteVersion) throws VersionMismatchException;
}
