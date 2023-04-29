package at.fhv.se.collabnotes.integration.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;
import at.fhv.se.collabnotes.utils.TestingUtils;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
@Transactional
public class NotesViewIntegrationTests {
    
    private WebClient webClient;

    @Autowired
    private NoteRepository noteRepository;
    
    @MockBean
    private StatisticsRepository statsRepo;
    
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
    void given_3notes_when_loadingroot_then_expectmatchingtitle_and_3matchinnotes() throws Exception {
        // given
        Mockito.when(statsService.statsInfo())
            .thenReturn(
                StatisticsDTO
                    .create()
                    .build());

        List<String> noteTitlesExpected = Arrays.asList("TODO 1", "TODO 2", "TODO 3");
        List<Note> notesExpected = noteTitlesExpected.stream()
            .map(title -> Note.create(title, this.noteRepository.nextIdentity()))
            .collect(Collectors.toList());

        notesExpected.forEach(this.noteRepository::add);

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/");
        
        // then
        assertEquals("Collab Notes", page.getTitleText(), "Mismatching page title text");

        final List<HtmlAnchor> lis = page.getByXPath("//ul//a");
        TestingUtils.assertEqualsCollections(lis, noteTitlesExpected, (i, title) -> {
            assertEquals(title, i.asText());
        });
    }

    @Test
    void given_nonotes_when_loadingroot_then_expectmatchingtitle_and_nomatchingnotes() throws Exception {
        // given
        Mockito.when(statsService.statsInfo())
        .thenReturn(
            StatisticsDTO
                .create()
                .build());

        // when
        HtmlPage page = this.webClient.getPage("http://localhost/");

        // then
        assertEquals("Collab Notes", page.getTitleText(), "Mismatching page title text");
        final List<HtmlListItem> lis = page.getByXPath("//ul//a");
        assertTrue(lis.isEmpty());
    }
}
