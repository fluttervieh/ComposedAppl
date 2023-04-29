package at.fhv.se.collabnotes.integration.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.NoteTitleDTO;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;
import at.fhv.se.collabnotes.infrastructure.HibernateEventRepository;
import at.fhv.se.collabnotes.utils.TestingUtils;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
public class NotesViewTests {
    
    private WebClient webClient;

    @MockBean
    private ViewNoteService viewNoteService;
    
    @MockBean
    private StatisticsRepository statsRepo;
    
    @MockBean
    private HibernateEventRepository eventRepo;

    @MockBean
    private StatisticsService statsService;
    
    @BeforeEach
    void setup(WebApplicationContext context) {
        this.webClient = MockMvcWebClientBuilder
                        .webAppContextSetup(context)
                        .build();
    }

    @AfterEach
    void teardown() {
        this.webClient.close();
    }
    
    @Test
    void given_3notes_when_fetchroot_then_show3notes() throws Exception {
        // given...
        Mockito.when(statsService.statsInfo())
        .thenReturn(
            StatisticsDTO
                .create()
                .build());

        List<NoteTitleDTO> noteTitles = Arrays.asList(
            new NoteTitleDTO("id1", "TODO 1"),
            new NoteTitleDTO("id2", "TODO 2"),
            new NoteTitleDTO("id3", "TODO 3"));
        Mockito.when(viewNoteService.allNoteTitles()).thenReturn(noteTitles);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/");

        // then
        assertEquals("Collab Notes", page.getTitleText(), "Mismatching page title text");

        final List<HtmlAnchor> lis = page.getByXPath("//ul//a");
        TestingUtils.assertEqualsCollections(lis, noteTitles, (i, dto) -> {
            assertEquals(dto.getTitle(), i.asText());
        });
    }

    @Test
    void given_nonotes_when_fetchroot_then_shownotnotes() throws Exception {
        // given...
        Mockito.when(statsService.statsInfo())
        .thenReturn(
            StatisticsDTO
                .create()
                .build());

        Mockito.when(viewNoteService.allNoteTitles()).thenReturn(Collections.emptyList());

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/");

        // then
        assertEquals("Collab Notes", page.getTitleText(), "Mismatching page title text");

        final List<HtmlAnchor> lis = page.getByXPath("//ul//a");
        assertEquals(0, lis.size(), "Expected 0 notes");
    }

    @Test
    void given_notewith3items_when_fetchingviewnote_then_show3items() throws Exception {
        // given
        String noteId = "42";
        NoteDTO noteDto = NoteDTO
            .create()
            .setId(noteId)
            .setTitle("TODO")
            .addItem("Item 1")
            .addItem("Item 2")
            .addItem("Item 3")
            .build();

        Mockito.when(viewNoteService.viewNote(noteDto.getId())).thenReturn(Optional.of(noteDto));

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/viewnote?id=" + noteId);
       
        // when
        assertEquals("Collab Notes", page.getTitleText(), "Mismatching page title text");

        final HtmlHeading1 title = page.getFirstByXPath("//h1");
        assertEquals(noteDto.getTitle(), title.asText());

        final List<HtmlListItem> lis = page.getByXPath("//li");
        TestingUtils.assertEqualsCollections(lis, noteDto.getItems(), (i, dto) -> {
            assertEquals(dto, i.asText());    
        });
    }

    @Test
    void view_note_not_existing() throws Exception {
        // given
        String noteId = "42";
        Mockito.when(viewNoteService.viewNote(noteId)).thenReturn(Optional.empty());

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/viewnote?id=" + noteId);

        // then
        assertEquals("Collab Notes", page.getTitleText(), "Mismatching page title text");

        final HtmlHeading1 title = page.getFirstByXPath("//h1");
        assertEquals("Error: Note not found!", title.asText());
    }
}
