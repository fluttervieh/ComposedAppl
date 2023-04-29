package at.fhv.se.collabnotes.application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import io.swagger.v3.oas.annotations.media.Schema;

public class NoteDTO {
    @Schema(required = true)
    private String id;
    @Schema(required = true)
    private int version;
    @Schema(required = true)
    private String title;
    @Schema(required = true)
    private List<String> items;

    private NoteDTO() {
        this.items = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public int getVersion() {
		return this.version;
	}

    public String getTitle() {
        return this.title;
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public static NoteDTO fromNote(Note note) {
        return NoteDTO.create()
					.setId(note.noteId().id())
					.setVersion(note.version())
					.setTitle(note.title())
					.addItems(note.items())
					.build();
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private NoteDTO instance;

        private Builder() {
            this.instance = new NoteDTO();
        }

        public Builder setId(String id) {
            this.instance.id = id;
            return this;
        }

        public Builder setVersion(int version) {
            this.instance.version = version;
            return this;
        }
        
        public Builder setTitle(String title) {
            this.instance.title = title;
            return this;
        }

        public Builder addItem(String item) {
            this.instance.items.add(item);
            return this;
        }

        public Builder addItems(Set<NoteItem> items) {
            for (NoteItem i : items) {
                this.instance.items.add(i.text());
            }

            return this;
        }

        public NoteDTO build() {
            if (null == this.instance.id) {
                throw new RuntimeException("Missing mandatory id, cannot build NoteDTO!");
            }

            if (null == this.instance.title) {
                throw new RuntimeException("Missing mandatory title, cannot build NoteDTO!");
            }

            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + version;
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
        NoteDTO other = (NoteDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    
}
