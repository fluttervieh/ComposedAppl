package at.fhv.se.collabnotes.application.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.model.NoteItem;
import at.fhv.se.collabnotes.domain.model.Statistics;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsRepository statsRepo;

    @Transactional
    @Override
    public void newItem(NoteItem item) {
        Statistics stats = statsRepo.fetchStatistics();
        stats.incrementItems();
    }

    @Transactional
    @Override
    public void newNote(NoteId noteId) {
        Statistics stats = statsRepo.fetchStatistics();
        stats.incrementNotes();
    }

    @Transactional
    @Override
    public void removeItem(NoteItem item) {
        Statistics stats = statsRepo.fetchStatistics();
        stats.decrementItems();
    }

    @Transactional
    @Override
    public void removeNote(NoteId noteId, int noteItemCount) {
        Statistics stats = statsRepo.fetchStatistics();
        stats.decrementNotes(noteItemCount);
    }

    @Transactional(readOnly = true)
    @Override
    public StatisticsDTO statsInfo() {
        Statistics stats = statsRepo.fetchStatistics();

        return StatisticsDTO
                .create()
                .setItemCount(stats.itemCount())
                .setNoteCount(stats.noteCount())
                .setAverageItems(stats.averageItems())
                .build();
    }
}
