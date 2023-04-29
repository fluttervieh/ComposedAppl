package at.fhv.se.collabnotes.integration.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.model.Statistics;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

@SpringBootTest
public class StatisticsServiceTests {
    
    @Autowired
    private StatisticsService statService;

    @MockBean
    private StatisticsRepository statsRepository;

    @Test
    void given_newitem_when_newitem_reflectchangesinstatistics() {
        // given
        NoteId noteId = new NoteId("42");
        NoteItem item = new NoteItem(noteId, "item");
        Statistics stats = Statistics.create(1, 0);
        Statistics statsExpected = Statistics.create(1, 1);
        Mockito.when(statsRepository.fetchStatistics()).thenReturn(stats);

        // when
        statService.newItem(item);

        // then
        Mockito.verify(statsRepository, times(1)).fetchStatistics();
        // stats aggregate has been mutated        
        assertEquals(statsExpected, stats);
    }

    @Test
    void given_newnote_when_newitem_reflectchangesinstatistics() {
        // given
        NoteId noteId = new NoteId("42");
        Statistics statsInit = Statistics.create(1, 0);
        Statistics statsExpected = Statistics.create(2, 0);
        Mockito.when(statsRepository.fetchStatistics()).thenReturn(statsInit);

        // when
        statService.newNote(noteId);

        // then
        Mockito.verify(statsRepository, times(1)).fetchStatistics();
        // stats aggregate has been mutated        
        assertEquals(statsExpected, statsInit);
    }

    @Test
    void given_item_when_removeitem_reflectchangesinstatistics() {
        // given
        NoteId noteId = new NoteId("42");
        NoteItem item = new NoteItem(noteId, "item");
        Statistics statsInit = Statistics.create(1, 1);
        Statistics statsExpected = Statistics.create(1, 0);
        Mockito.when(statsRepository.fetchStatistics()).thenReturn(statsInit);

        // when
        statService.removeItem(item);

        // then
        Mockito.verify(statsRepository, times(1)).fetchStatistics();
        // stats aggregate has been mutated        
        assertEquals(statsExpected, statsInit);
    }

    @Test
    void given_note_when_removenote_reflectchangesinstatistics() {
        // given
        NoteId noteId = new NoteId("42");
        int noteItemCount = 10;
        Statistics statsInit = Statistics.create(1, 10);
        Statistics statsExpected = Statistics.create(0, 0);
        Mockito.when(statsRepository.fetchStatistics()).thenReturn(statsInit);

        // when
        statService.removeNote(noteId, noteItemCount);

        // then
        Mockito.verify(statsRepository, times(1)).fetchStatistics();
        // stats aggregate has been mutated
        assertEquals(statsExpected, statsInit);
    }

    @Test
    void given_statsinrepo_when_fetch_then_servicereturnsdto() {
        // given
        Statistics stats = Statistics.create(3, 42);
        Mockito.when(statsRepository.fetchStatistics()).thenReturn(stats);

        // when
        StatisticsDTO statsDto = statService.statsInfo();

        // then
        Mockito.verify(statsRepository, times(1)).fetchStatistics();
        
        assertEquals(statsDto.getItems(), stats.itemCount());
        assertEquals(statsDto.getNotes(), stats.noteCount());
        assertEquals(statsDto.getAverageItems(), stats.averageItems());
    }
}
