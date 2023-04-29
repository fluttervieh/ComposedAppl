package at.fhv.se.collabnotes.domain.events;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;

public class NoteCreated implements DomainEvent {
    private NoteId noteId;
    private String noteTitle;

    public static NoteCreated fromNote(Note note) {
        return new NoteCreated(note.noteId(), note.title());
    }

    public NoteCreated(NoteId noteId, String noteTitle) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
    }

	public NoteId noteId() {
		return noteId;
	}

	public String noteTitle() {
		return noteTitle;
	}

    // NOTE: need for JSON deserialisation
    @SuppressWarnings("unused")
    private NoteCreated() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((noteId == null) ? 0 : noteId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NoteCreated other = (NoteCreated) obj;
        if (noteId == null) {
            if (other.noteId != null)
                return false;
        } else if (!noteId.equals(other.noteId))
            return false;
        return true;
    }
}
