package at.fhv.se.collabnotes.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import at.fhv.se.collabnotes.domain.model.Statistics;

public class StatisticsTests {
    
    @Test
    void given_initalcounts_when_incrementitems_then_itemsplus1() {
        // given
        int initNoteCount = 1;
        int initItemCount = 42;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when
        stats.incrementItems();

        // then
        assertEquals(initNoteCount, stats.noteCount());
        assertEquals(initItemCount + 1, stats.itemCount());
    }

    @Test
    void given_initalcounts_when_decrementitems_then_itemsminus1() {
        // given
        int initNoteCount = 1;
        int initItemCount = 42;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when
        stats.decrementItems();

        // then
        assertEquals(initNoteCount, stats.noteCount());
        assertEquals(initItemCount - 1, stats.itemCount());
    }

    @Test
    void given_initalcounts_when_decrementitemstonegative_then_throws() {
        // given
        int initNoteCount = 1;
        int initItemCount = 0;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when ... then
        assertThrows(IllegalStateException.class, () -> stats.decrementItems());
    }

    @Test
    void given_initalcounts_when_incrementnotes_then_notesplus1() {
        int initNoteCount = 1;
        int initItemCount = 42;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        stats.incrementNotes();

        assertEquals(initNoteCount + 1, stats.noteCount());
        assertEquals(initItemCount, stats.itemCount());
    }

    @Test
    void given_initalcounts_when_decrementtnotes_then_notesminus1_and_itemsminusnotesize() {
        // given
        int initNoteCount = 2;
        int itemsOfNote = 10;
        int initItemCount = 42;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when
        stats.decrementNotes(itemsOfNote);

        // then
        assertEquals(initNoteCount - 1, stats.noteCount());
        assertEquals(initItemCount - itemsOfNote, stats.itemCount());
    }

    @Test
    void given_initalnotecount_when_decrementnotes_but_invaliditems_expectthrows() {
        // given
        int initNoteCount = 1;
        int initItemCount = 10;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when ... then
        assertThrows(IllegalStateException.class, () -> stats.decrementNotes(0));
    }

    @Test
    void given_initalnotecount_when_decrementnotestonegative_then_expectthrows() {
        // given
        int initNoteCount = 0;
        int initItemCount = 0;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when ... then
        assertThrows(IllegalStateException.class, () -> stats.decrementNotes(0));
    }

    @Test
    void given_initalnotecount_when_decrementitemstonegative_then_expectthrows() {
        // given
        int initNoteCount = 1;
        int initItemCount = 5;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when ... then
        assertThrows(IllegalStateException.class, () -> stats.decrementNotes(10));
    }

    @Test
    void given_initalcounts_when_average_then_correct() {
        // given
        int initNoteCount = 10;
        int initItemCount = 100;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when
        double avgActual = stats.averageItems();

        // then
        assertEquals(10, avgActual);
    }


    @Test
    void given_initalnotecount0_when_average_then_0() {
        // given
        int initNoteCount = 0;
        int initItemCount = 0;
        Statistics stats = Statistics.create(initNoteCount, initItemCount);
        
        // when
        double avgActual = stats.averageItems();

        // then
        assertEquals(0, avgActual);
    }

    @Test
    void given_initial0notes_butnon0items_when_create_expectthrows() {
        // given
        int initNoteCount = 0;
        int initItemCount = 10;
        
        // when ... then
        assertThrows(IllegalStateException.class, () -> Statistics.create(initNoteCount, initItemCount));
    }
}
