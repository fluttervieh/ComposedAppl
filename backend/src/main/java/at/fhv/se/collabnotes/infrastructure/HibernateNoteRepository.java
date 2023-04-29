package at.fhv.se.collabnotes.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import at.fhv.se.collabnotes.domain.model.Note;
import at.fhv.se.collabnotes.domain.model.NoteId;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;

public class HibernateNoteRepository implements NoteRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Note> findAllNotes() {
        TypedQuery<Note> query = entityManager.createQuery("SELECT n FROM Note n", Note.class);
        return query.getResultList();
    }

    @Override
    public NoteId nextIdentity() {
        return new NoteId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void add(Note note) {
        entityManager.persist(note);
    }

    @Override
    public Optional<Note> noteById(NoteId noteId) {
        TypedQuery<Note> query = entityManager.createQuery("from Note as n where n.noteId = :noteId", Note.class);
        query.setParameter("noteId", noteId);

        // note: getSingleResult throws an error if there is none
        List<Note> result = query.getResultList();
        if (1 != result.size()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public void delete(Note note) {
        entityManager.remove(note);
    }
}
