package at.fhv.se.collabnotes.application;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import at.fhv.se.collabnotes.application.impl.CreateNoteServiceImpl;
import at.fhv.se.collabnotes.application.impl.EventProcessingServiceImpl;
import at.fhv.se.collabnotes.application.impl.StatisticsServiceImpl;
import at.fhv.se.collabnotes.application.impl.ViewNoteServiceImpl;

@Component
public class ApplicationFacade {
    
    private ApplicationFacade() {}

    @Bean
    public static ViewNoteService viewNotesService() {
        return new ViewNoteServiceImpl();
    }

    @Bean
    public static CreateNoteService createNoteService() {
        return new CreateNoteServiceImpl();
    }

    @Bean
    public static StatisticsService statisticsService() {
        return new StatisticsServiceImpl();
    }

    @Bean
    public static EventProcessingService eventProcessingService() {
        return new EventProcessingServiceImpl();
    }
}
