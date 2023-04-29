package at.fhv.se.collabnotes.unit.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import at.fhv.se.collabnotes.domain.events.DomainEventSubscriber;
import at.fhv.se.collabnotes.domain.events.ItemAdded;
import at.fhv.se.collabnotes.domain.events.ItemRemoved;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.model.VersionMismatchException;

public class NoteTests {
    
    @Test
    void when_create_correct() {
        // given
        String title = "TODO";
        String idStr = "42";
        NoteId id = new NoteId(idStr);

        // when
        Note note = Note.create(title, id);

        // then
        assertEquals(title, note.title());
        assertEquals(id, note.noteId());
        assertEquals(idStr, note.noteId().id());
    }

    @Test
    void check_NoteId_equals() {
        // given
        NoteId id0_1 = new NoteId("0");
        NoteId id0_2 = new NoteId("0");
        NoteId id1 = new NoteId("1");

        // when...then
        assertNotEquals(id0_1, id1);
        assertNotEquals(id0_2, id1);
        assertEquals(id0_1, id0_2, "both NoteIds should be equals");
    }

    @Test
    void test_items_unmodifiable() {
        // given
        NoteId noteId = new NoteId("42");
        Note note = Note.create("Todo", noteId);
       
        // when ... then
        assertThrows(UnsupportedOperationException.class, () -> note.items().add(new NoteItem(noteId, "Item 1")));
    }

    @Test
    void test_NoteItem_equals() {
        // given
        NoteId noteId = new NoteId("42");
        NoteItem item1 = new NoteItem(noteId, "Item 1");
        NoteItem item2 = new NoteItem(noteId, "Item 2");
        NoteItem item1_same = new NoteItem(noteId, "Item 1");

        // when...then
        assertNotEquals(item1, item2);
        assertNotEquals(item2, item1_same);
        assertEquals(item1, item1_same, "both NoteIds should be equals");
    }

    @Test
    void test_item_added_returned() {
        // given
        NoteId noteId = new NoteId("42");
        Note note = Note.create("Todo", noteId);
        NoteItem item = new NoteItem(noteId, "Item 1");
    
        // when
        note.addItem(item);
        
        // then
        assertEquals(item, note.items().iterator().next());
    }

    @Test
    void test_item_removed_empty() {
        // given
        NoteId noteId = new NoteId("42");
        Note note = Note.create("Todo", noteId);
        NoteItem item = new NoteItem(noteId, "Item 1");
    
        // when
        note.addItem(item);
        assertEquals(item, note.items().iterator().next());

        note.removeItem(item);

        // then
        assertTrue(note.items().isEmpty());
    }

    @Test
    void given_note_when_version_match_same_then_no_exception() {
        // given
        Note note = Note.create("Todo", new NoteId("42"));
        
        // when ... then
        assertDoesNotThrow(() -> note.matchVersion(0));
    }

    @Test
    void given_note_when_version_match_differ_then_exception() {
        // given
        Note note = Note.create("Todo", new NoteId("42"));
        
        // when ... then
        assertThrows(VersionMismatchException.class, () -> note.matchVersion(1));
    }

    @Test
    void given_eventpublisher_when_newitem_then_expecteventpublished() {
        // given
        String noteTitle = "TODO";
        NoteId noteId = new NoteId("42");
        Note note = Note.create(noteTitle, noteId);
        NoteItem item = new NoteItem(noteId, "Item1");

        DomainEventSubscriber<ItemAdded> subscriber = new DomainEventSubscriber<>() {
            @Override
            public void handleEvent(ItemAdded event) {
                assertEquals(event.noteId(), noteId);
                assertEquals(event.itemText(), item.text());
            }
            @Override
            public Class<ItemAdded> subscribedToEventType() {
                return ItemAdded.class;
            }
        };
        DomainEventSubscriber<ItemAdded> subscriberSpy = Mockito.spy(subscriber);
        note.subscribe(subscriberSpy);
        
        // when
        note.addItem(item);

        // then
        Mockito.verify(subscriberSpy, times(1)).handleEvent(any());
    }

    @Test
    void given_eventpublisher_when_removeitem_then_expecteventpublished() {
        // given
        String noteTitle = "TODO";
        NoteId noteId = new NoteId("42");
        Note note = Note.create(noteTitle, noteId);
        NoteItem item = new NoteItem(noteId, "Item1");

        DomainEventSubscriber<ItemRemoved> subscriber = new DomainEventSubscriber<>() {
            @Override
            public void handleEvent(ItemRemoved event) {
                assertEquals(event.noteId(), noteId);
                assertEquals(event.itemText(), item.text());
            }
            @Override
            public Class<ItemRemoved> subscribedToEventType() {
                return ItemRemoved.class;
            }
        };
        DomainEventSubscriber<ItemRemoved> subscriberSpy = Mockito.spy(subscriber);
        note.subscribe(subscriberSpy);
        
        // when
        note.removeItem(item);

        // then
        Mockito.verify(subscriberSpy, times(1)).handleEvent(any());
    }
}
