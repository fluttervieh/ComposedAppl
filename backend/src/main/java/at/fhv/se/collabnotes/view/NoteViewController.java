package at.fhv.se.collabnotes.view;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.NoteTitleDTO;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;
import at.fhv.se.collabnotes.view.form.AddItemForm;

@Controller
public class NoteViewController {
    private static final String PREFIX = "/mvc";

    private static final String ALL_NOTES_URL = PREFIX + "/";
    private static final String NEW_NOTE_URL = PREFIX + "/newnote";
    private static final String CREATE_NOTE_URL = PREFIX + "/createnote";
    private static final String VIEW_NOTE_URL = PREFIX + "/viewnote";
    private static final String ADD_ITEM_URL = PREFIX + "/additem";
    private static final String REMOVE_ITEM_URL = PREFIX + "/removeitem";
    private static final String DELETE_NOTE_URL = PREFIX + "/deletenote";
    
    private static final String ERROR_URL = PREFIX + "/displayerror";

    private static final String ALL_NOTES_VIEW = "allNotes";
    private static final String NEW_NOTE_VIEW = "newNote";
    private static final String VIEW_NOTE_VIEW = "viewNote";
    private static final String ERROR_VIEW = "errorView";

    @Autowired
    private ViewNoteService viewNoteService;

    @Autowired
    private CreateNoteService createNoteService;

    @Autowired
    private StatisticsService statsService;

    @GetMapping(ALL_NOTES_URL)
    public String allNotes(Model model) {
        final List<NoteTitleDTO> allNotes = viewNoteService.allNoteTitles();
        final StatisticsDTO statsDto = statsService.statsInfo();
        
        model.addAttribute("allNotes", allNotes);
        model.addAttribute("stats", statsDto);

        return ALL_NOTES_VIEW;
    }

    @GetMapping(NEW_NOTE_URL)
    public String newNote(Model model) {
        return NEW_NOTE_VIEW;
    }

    @PostMapping(CREATE_NOTE_URL)
    public ModelAndView newNote(@RequestParam String title) {
        NoteId noteId = this.createNoteService.createNote(title);
        return new ModelAndView("redirect:" + VIEW_NOTE_URL + "?id=" + noteId.id());
    }

    @GetMapping(VIEW_NOTE_URL)
    public ModelAndView viewNote(@RequestParam("id") String noteId, Model model) {
        Optional<NoteDTO> noteDtoOpt = viewNoteService.viewNote(noteId);

        if (noteDtoOpt.isPresent()) {
            NoteDTO noteDto = noteDtoOpt.get();

            model.addAttribute("note", noteDto);
            model.addAttribute("addItem", new AddItemForm(noteDto.getId(), noteDto.getVersion(), ""));
            return new ModelAndView(VIEW_NOTE_VIEW);

        } else {
            String msg = "Note not found!";
            return redirectError(msg);
        }
    }

    @PostMapping(ADD_ITEM_URL)
    public ModelAndView addItem(@ModelAttribute AddItemForm addItemForm, Model model) {
        try {
            this.createNoteService.addItem(
                addItemForm.getNoteId(), 
                addItemForm.getNoteVersion(),
                addItemForm.getItemText());
        } catch (VersionMismatchException e) {
            String msg = "Concurrent note modification!";
            return redirectError(msg);
        }

        return new ModelAndView("redirect:" + VIEW_NOTE_URL + "?id=" + addItemForm.getNoteId());
    }

    @PostMapping(REMOVE_ITEM_URL)
    public ModelAndView removeItem(@ModelAttribute AddItemForm addItemForm, Model model) {
        try {
            this.createNoteService.removeItem(
                addItemForm.getNoteId(), 
                addItemForm.getNoteVersion(),
                addItemForm.getItemText());
        } catch (VersionMismatchException e) {
            String msg = "Concurrent note modification!";
            return redirectError(msg);
        }

        return new ModelAndView("redirect:" + VIEW_NOTE_URL + "?id=" + addItemForm.getNoteId());
    }

    @PostMapping(DELETE_NOTE_URL)
    public ModelAndView deleteNote(@RequestParam("id") String noteId, @RequestParam("version") String noteVersion, Model model) {
        try {
            this.createNoteService.deleteNote(
                noteId, 
                Integer.parseInt(noteVersion));
        } catch (NumberFormatException e) {
            String msg = "Could not parse note version '" +  noteVersion + "'";
            return redirectError(msg);
        } catch (VersionMismatchException e) {
            String msg = "Concurrent note modification!";
            return redirectError(msg);
        }

        return new ModelAndView("redirect:" + ALL_NOTES_URL);
    }

    @GetMapping(ERROR_URL)
    public String displayError(@RequestParam("message") String message, Model model) {
        model.addAttribute("message", message);
        return ERROR_VIEW;
    }

    private static ModelAndView redirectError(String msg) {
        return new ModelAndView("redirect:" + ERROR_URL + "?message=" + msg);
    } 
}
