package at.fhv.se.collabnotes.integration.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.web.util.UriComponentsBuilder;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;
import at.fhv.se.collabnotes.rest.data.UpdateResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestApiTests {
    
    @LocalServerPort
	private int port;
    
    @Autowired
	private TestRestTemplate restTemplate;

    @MockBean
    private ViewNoteService viewNoteService;
    
    @MockBean
    private StatisticsService statsService;

    @MockBean
    private CreateNoteService createNoteService;

    @Test
	public void given_notes_when_fetchallnotesthroughrest_then_returnequalsnotes() throws Exception {
        // given
        List<NoteDTO> noteDTOs = Arrays.asList(
            NoteDTO.create()
                .setId("1")
                .setVersion(1)
                .setTitle("TODO 1")
                .addItem("Item 1")
                .addItem("Item 2")
                .addItem("Item 3")
                .build(),
            NoteDTO.create()
                .setId("2")
                .setVersion(1)
                .setTitle("TODO 2")
                .addItem("Item A")
                .addItem("Item B")
                .addItem("Item C")
                .build());
        Mockito.when(viewNoteService.allNotes()).thenReturn(noteDTOs);

        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
            .path("/rest/note/all").build().encode().toUri();
        NoteDTO[] restNoteTitles = this.restTemplate.getForObject(uri, NoteDTO[].class);

        // then
        for (int i = 0; i < restNoteTitles.length; i++) {
            assertEquals(noteDTOs.get(i), restNoteTitles[i]);
        }
	}

    @Test
	public void given_statistics_when_fetchstatsthroughrest_then_returnequalsstatistics() throws Exception {
        // given
        StatisticsDTO statsDTO = StatisticsDTO.create()
            .setNoteCount(2)
            .setItemCount(42)
            .setAverageItems(21)
            .build();
        Mockito.when(statsService.statsInfo()).thenReturn(statsDTO);

        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
            .path("/rest/stats").build().encode().toUri();
        StatisticsDTO restStatsDTO = this.restTemplate.getForObject(uri, StatisticsDTO.class);

        // then
        assertEquals(statsDTO, restStatsDTO);
	}

    @Test
	public void given_restservice_when_createnewnote_then_returnnewnoteid() throws Exception {
        // given
        String noteTitle = "TODO";
        NoteId noteId = new NoteId("42");
        Mockito.when(createNoteService.createNote(noteTitle)).thenReturn(noteId);

        // when
        HttpEntity<String> request = RestTestUtils.requestWithPlainText(noteTitle);

        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
            .path("/rest/note/")
            .build().encode().toUri();
        String restNoteId = this.restTemplate.postForObject(uri, request, String.class);

        // then
        assertEquals(noteId.id(), restNoteId);
	}

    @Test
	public void given_restservice_when_deletenote_then_returnokinfo() throws Exception {
        // given
        int noteVersion = 1;
        String noteId = "42";

        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
            .path("/rest/note/")
            .pathSegment(noteId)
            .pathSegment("" + noteVersion)
            .build().encode().toUri();
        this.restTemplate.delete(uri);

        // then
        Mockito.verify(createNoteService, times(1)).deleteNote(noteId, noteVersion);
	}

    @Test
	public void given_restservice_when_deletenote_mismatchversion_then_returnerrorinfo() throws Exception {
        // given
        int noteVersion = 1;
        String noteId = "42";

        Mockito.doThrow(VersionMismatchException.class).when(createNoteService).deleteNote(noteId, noteVersion);

       // when
       URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
                    .path("/rest/note/")
                    .pathSegment(noteId)
                    .pathSegment("" + noteVersion)
                    .build().encode().toUri();
        this.restTemplate.delete(uri);

        // then
        Mockito.verify(createNoteService, times(1)).deleteNote(noteId, noteVersion);
	}

    @Test
	public void given_restservice_when_additem_then_returnokinfo() throws Exception {
        // given
        String noteId = "42";
        int noteVersion = 1;
        String itemText = "Item 1";
        UpdateResponse expectedResponse = UpdateResponse.ofOk();

        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
            .path("/rest/note/")
            .pathSegment(noteId)
            .pathSegment("" + noteVersion)
            .pathSegment("item")
            .pathSegment(itemText)
            .build().encode().toUri();
        UpdateResponse actualResponse = this.restTemplate.postForObject(uri, null, UpdateResponse.class);

        // then
        Mockito.verify(createNoteService, times(1)).addItem(noteId, noteVersion, itemText);

        assertEquals(expectedResponse, actualResponse);
	}

    @Test
	public void given_restservice_when_additem_mismatchingversion_then_returnerrorinfo() throws Exception {
        // given
        String noteId = "42";
        int noteVersion = 1;
        String itemText = "Item 1";
        UpdateResponse expectedResponse = UpdateResponse.ofError("Concurrent note modification!");

        Mockito.doThrow(VersionMismatchException.class).when(createNoteService).addItem(noteId, noteVersion, itemText);

        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
                    .path("/rest/note/")
                    .pathSegment(noteId)
                    .pathSegment("" + noteVersion)
                    .pathSegment("item")
                    .pathSegment(itemText)
                    .build().encode().toUri();
        UpdateResponse actualResponse = this.restTemplate.postForObject(uri, null, UpdateResponse.class);

        // then
        Mockito.verify(createNoteService, times(1)).addItem(noteId, noteVersion, itemText);
        assertEquals(expectedResponse, actualResponse);
	}

    @Test
	public void given_restservice_when_removeitem_then_returnokinfo() throws Exception {
        // given
        String noteId = "42";
        int noteVersion = 1;
        String itemText = "Item 1";
        
        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
                    .path("/rest/note/")
                    .pathSegment(noteId)
                    .pathSegment("" + noteVersion)
                    .pathSegment("item")
                    .pathSegment(itemText)
                    .build().encode().toUri();
        this.restTemplate.delete(uri);

        // then
        Mockito.verify(createNoteService, times(1)).removeItem(noteId, noteVersion, itemText);
	}

    @Test
	public void given_restservice_when_removeitem_mismatchinversion_then_returnerrorinfo() throws Exception {
        // given
        String noteId = "42";
        int noteVersion = 1;
        String itemText = "Item 1";
        
        Mockito.doThrow(VersionMismatchException.class).when(createNoteService).removeItem(noteId, noteVersion, itemText);

        // when
        URI uri = UriComponentsBuilder.fromUriString("http://localhost:" + port)
                    .path("/rest/note/")
                    .pathSegment(noteId)
                    .pathSegment("" + noteVersion)
                    .pathSegment("item")
                    .pathSegment(itemText)
                    .build().encode().toUri();
        this.restTemplate.delete(uri);

        // then
        Mockito.verify(createNoteService, times(1)).removeItem(noteId, noteVersion, itemText);
	}
}
