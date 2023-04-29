package at.fhv.se.collabnotes.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;
import at.fhv.se.collabnotes.rest.data.UpdateResponse;

@RestController
@RequestMapping("/rest/note")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NoteRestController {
    @Autowired
    private ViewNoteService viewNoteService;

    @Autowired
    private CreateNoteService createNoteService;

    @GetMapping("/all")
    @ResponseBody
	public NoteDTO[] allNotes() {
		final List<NoteDTO> allNotesList = viewNoteService.allNotes();
        NoteDTO[] allNotesArray = new NoteDTO[allNotesList.size()];
        return allNotesList.toArray(allNotesArray);
	}

    @PostMapping(value = "/")
    public String createNote(@RequestBody String title) {
        NoteId noteId = createNoteService.createNote(title);
        return noteId.id();
    }

    @DeleteMapping("/{id}/{version}")
    @ResponseBody
    public UpdateResponse deleteNote(@PathVariable("id") String noteId, @PathVariable("version") int noteVersion) {
        try {
            this.createNoteService.deleteNote(
                noteId, 
                noteVersion);

            return UpdateResponse.ofOk();
        } catch (VersionMismatchException e) {
            String msg = "Concurrent note modification!";
            return UpdateResponse.ofError(msg);
        }
    }

    @PostMapping("/{id}/{version}/item/{itemtext}")
    @ResponseBody
    public UpdateResponse addItem(@PathVariable("id") String noteId, @PathVariable("version") int noteVersion, @PathVariable("itemtext") String itemText) {
        try {
            this.createNoteService.addItem(
                noteId,
                noteVersion,
                itemText);

            return UpdateResponse.ofOk();
        } catch (VersionMismatchException e) {
            String msg = "Concurrent note modification!";
            return UpdateResponse.ofError(msg);
        }
    }

    @DeleteMapping("/{id}/{version}/item/{itemtext}")
    public UpdateResponse removeItem(@PathVariable("id") String noteId, @PathVariable("version") int noteVersion, @PathVariable("itemtext") String itemText) {
        try {
            this.createNoteService.removeItem(
                noteId,
                noteVersion,
                itemText);

            return UpdateResponse.ofOk();
        } catch (VersionMismatchException e) {
            String msg = "Concurrent note modification!";
            return UpdateResponse.ofError(msg);
        }
    }
}
