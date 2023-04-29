package at.fhv.se.collabnotes.domain.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import at.fhv.se.collabnotes.domain.events.DomainEventPublisher;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;

public class Note extends DomainEventPublisher {
    @SuppressWarnings("unused")
    private Long id; // this is the native DB id, only used by the underyling DB
    private int version;

    private String title;
    private NoteId noteId;
    private Set<NoteItem> items;

    public static Note create(String title, NoteId noteId) {
        return new Note(title, noteId);
	}

    public NoteId noteId() {
        return this.noteId;
    }

    public String title() {
        return this.title;
    }

    public Set<NoteItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    public int version() {
        return version;
    }

    public void matchVersion(int expected) throws VersionMismatchException {
        if (this.version() != expected) {
            throw new VersionMismatchException(this.version, expected);
        }
    }

    public void addItem(NoteItem item) {
        this.publish(new ItemAdded(this.noteId(), item.text()));
        this.items.add(item);
	}

    public void removeItem(NoteItem item) {
        this.publish(new ItemRemoved(this.noteId(), item.text()));
        this.items.remove(item);
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
        Note other = (Note) obj;
        if (noteId == null) {
            if (other.noteId != null)
                return false;
        } else if (!noteId.equals(other.noteId))
            return false;
        return true;
    }
    
    private Note(String title, NoteId noteId) {
        this();

        this.items = new LinkedHashSet<>();
        this.noteId = noteId;
        this.title = title;
    }

    // needed by Hibernate - but can keep it private
    private Note() {
    }
}
