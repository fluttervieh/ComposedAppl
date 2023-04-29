package at.fhv.se.collabnotes.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

@Component
public class HibernateRepositoriesFacade {

    @Bean
    public NoteRepository noteRepository() {
        return new HibernateNoteRepository();
    }

    @Bean
    public StatisticsRepository statisticsRepository() {
        return new HibernateStatisticsRepository();
    }

    @Bean
    public HibernateEventRepository eventRepository() {
        return new HibernateEventRepository();
    }
}
