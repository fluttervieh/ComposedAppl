package at.fhv.se.collabnotes.application;

import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;

public interface StatisticsService {

	void newItem(NoteItem item);
	
	void newNote(NoteId noteId);

	void removeItem(NoteItem item);

	void removeNote(NoteId noteId, int noteItemCount);

	StatisticsDTO statsInfo();
}
