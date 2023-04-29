package at.fhv.se.collabnotes.domain.model;

public class NoteItem {
    @SuppressWarnings("unused")
    private Long id; // used by hibernate
    private NoteId noteId;

    private String text;

    public NoteItem(NoteId noteId, String text) {
        this.text = text;
        this.noteId = noteId;
    }

    public String text() {
        return this.text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((noteId == null) ? 0 : noteId.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
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
        NoteItem other = (NoteItem) obj;
        if (noteId == null) {
            if (other.noteId != null)
                return false;
        } else if (!noteId.equals(other.noteId))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

    // needed by hibernate
    @SuppressWarnings("unused")
    private NoteItem() {
    }
}
