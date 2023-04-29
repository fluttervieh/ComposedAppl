package at.fhv.se.collabnotes.domain.events;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;

public class NoteDeleted implements DomainEvent {
    private NoteId noteId;
    private int itemCount;

    public static NoteDeleted fromNote(Note note) {
        return new NoteDeleted(note.noteId(), note.items().size());
    }

    public NoteDeleted(NoteId noteId, int itemCount) {
        this.noteId = noteId;
        this.itemCount = itemCount;
    }

	public NoteId noteId() {
		return noteId;
	}

    public int itemCount() {
        return this.itemCount;
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
        NoteDeleted other = (NoteDeleted) obj;
        if (noteId == null) {
            if (other.noteId != null)
                return false;
        } else if (!noteId.equals(other.noteId))
            return false;
        return true;
    }

    // NOTE: need for JSON deserialisation
    @SuppressWarnings("unused")
    private NoteDeleted() {
    }
}
