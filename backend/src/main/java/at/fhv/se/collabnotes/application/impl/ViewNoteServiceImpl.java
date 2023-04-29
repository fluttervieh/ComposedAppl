package at.fhv.se.collabnotes.application.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.application.dto.NoteDTO;
import at.fhv.se.collabnotes.application.dto.NoteTitleDTO;
import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;

public class ViewNoteServiceImpl implements ViewNoteService {

	@Autowired
	public NoteRepository noteRepository;

    @Transactional(readOnly = true)
	@Override
	public List<NoteTitleDTO> allNoteTitles() {
		List<Note> allNotes = noteRepository.findAllNotes();
		return allNotes
			.stream()
			.map(note -> 
				new NoteTitleDTO(note.noteId().id(), note.title()))
			.collect(Collectors.toList());
	}

    @Transactional(readOnly = true)
    @Override
    public List<NoteDTO> allNotes() {
        List<Note> allNotes = noteRepository.findAllNotes();
		return allNotes
			.stream()
			.map(NoteDTO::fromNote)
			.collect(Collectors.toList());
    }

	@Transactional(readOnly = true)
	@Override
	public Optional<NoteDTO> viewNote(String noteId) {
		Optional<Note> noteOpt = noteRepository.noteById(new NoteId(noteId));
		return noteOpt.map(NoteDTO::fromNote);
	}
}
