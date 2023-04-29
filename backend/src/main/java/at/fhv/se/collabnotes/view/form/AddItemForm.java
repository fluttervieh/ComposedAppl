package at.fhv.se.collabnotes.view.form;

import io.swagger.v3.oas.annotations.media.Schema;

public class AddItemForm {
    @Schema(required = true)
    private String noteId;
    @Schema(required = true)
    private String itemText;
    @Schema(required = true)
    private int noteVersion;

    public AddItemForm(String noteId, int noteVersion, String itemText) {
        this.noteId = noteId;
        this.itemText = itemText;
        this.noteVersion = noteVersion;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public int getNoteVersion() {
        return noteVersion;
    }

    public void setNoteVersion(int noteVersion) {
        this.noteVersion = noteVersion;
    }

    // NOTE: needed for JSON
    @SuppressWarnings("unused")
    private AddItemForm() {}
}
