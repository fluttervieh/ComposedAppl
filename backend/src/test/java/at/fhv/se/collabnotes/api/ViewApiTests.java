package at.fhv.se.collabnotes.api;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class ViewApiTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteRepository noteRepository;

    @MockBean
    private CreateNoteService createNoteService;

    @MockBean
    private ViewNoteService viewNoteService;
    
    @MockBean
    private StatisticsRepository statsRepo;

    @MockBean
    private StatisticsService statsService;

    @Test
    public void when_rooturl_then_statusok_and_allNotesView_and_viewnotesservice_called() throws Exception {
        // given...
        Mockito.when(statsService.statsInfo())
            .thenReturn(
                StatisticsDTO
                    .create()
                    .build());

        // when ... then
        this.mockMvc.perform(get("/").accept(org.springframework.http.MediaType.TEXT_PLAIN))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("allNotes"));

        // then
        Mockito.verify(viewNoteService, times(1)).allNoteTitles();
    }

    @Test
    public void test_newNote_api() throws Exception {
        this.mockMvc.perform(get("/newnote")
            .accept(org.springframework.http.MediaType.TEXT_PLAIN))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("newNote"));
    }

    @Test
    public void test_createNote_api() throws Exception {
        String noteTitle = "TODO";
        NoteId noteId = new NoteId("42");

        Mockito.when(createNoteService.createNote(noteTitle)).thenReturn(noteId);

        this.mockMvc.perform(post("/createnote")
            .content("title=" + noteTitle)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/viewnote?id=" + noteId.id()));
        
        Mockito.verify(createNoteService, times(1)).createNote(noteTitle);
    }

    //@Test
    public void test_viewNote_fail_api() throws Exception {
        String noteId = "42";

        Mockito.when(viewNoteService.viewNote(noteId)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/viewnote")
            .param("id", noteId)
            .accept(org.springframework.http.MediaType.TEXT_PLAIN))
            .andDo(print())
            .andExpect(status().isFound())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("redirect:/displayError"));
    }

    @Test
    public void test_viewNote_success_api() throws Exception {
        String noteId = "42";
        int noteVersion = 1;

        NoteDTO noteDto = NoteDTO
            .create()
            .setId(noteId)
            .setVersion(noteVersion)
            .setTitle("TODO")
            .build();

        Mockito.when(viewNoteService.viewNote(noteId)).thenReturn(Optional.of(noteDto));

        this.mockMvc.perform(get("/viewnote")
            .param("id", noteId)
            .param("version", ""+noteVersion)
            .accept(org.springframework.http.MediaType.TEXT_PLAIN))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("viewNote"));
    }

    @Test
    public void test_addItem_api() throws Exception {
        String noteId = "42";
        int noteVersion = 1;
        String itemText = "Item 1";

        this.mockMvc.perform(post("/additem")
            .content("noteId=" + noteId + "&itemText=" + itemText + "&noteVersion=" + noteVersion)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/viewnote?id=" + noteId));
        
        Mockito.verify(createNoteService, times(1)).addItem(noteId, noteVersion, itemText);
    }

    @Test
    public void test_removeItem_api() throws Exception {
        String noteId = "42";
        int noteVersion = 1;
        String itemText = "Item 1";

        this.mockMvc.perform(post("/removeitem")
            .content("noteId=" + noteId + "&itemText=" + itemText + "&noteVersion=" + noteVersion)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/viewnote?id=" + noteId));
        
        Mockito.verify(createNoteService, times(1)).removeItem(noteId, noteVersion, itemText);
    }

    @Test
    public void test_remove_note_api() throws Exception {
        String noteId = "42";
        int noteVersion = 1;

        this.mockMvc.perform(post("/deletenote")
            .content("id=" + noteId + "&version=" + noteVersion)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/"));
        
        Mockito.verify(createNoteService, times(1)).deleteNote(noteId, noteVersion);
    }

    @Test
    public void test_error_api() throws Exception {
        this.mockMvc.perform(get("/displayerror")
            .param("message", "testmessage")
            .accept(org.springframework.http.MediaType.TEXT_PLAIN))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("errorView"));
    }
}