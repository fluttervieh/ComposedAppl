package at.fhv.se.collabnotes.domain.events;

import at.fhv.se.collabnotes.domain.model.NoteId;

public class ItemRemoved implements DomainEvent {
    private NoteId noteId;
    private String itemText;

    public ItemRemoved(NoteId noteId, String itemText) {
        this.noteId = noteId;
        this.itemText = itemText;
    }

	public NoteId noteId() {
		return this.noteId;
	}

	public String itemText() {
		return this.itemText;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((itemText == null) ? 0 : itemText.hashCode());
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
        ItemRemoved other = (ItemRemoved) obj;
        if (itemText == null) {
            if (other.itemText != null)
                return false;
        } else if (!itemText.equals(other.itemText))
            return false;
        if (noteId == null) {
            if (other.noteId != null)
                return false;
        } else if (!noteId.equals(other.noteId))
            return false;
        return true;
    }

    // NOTE: need for JSON deserialisation
    @SuppressWarnings("unused")
    private ItemRemoved() {
    }
}
