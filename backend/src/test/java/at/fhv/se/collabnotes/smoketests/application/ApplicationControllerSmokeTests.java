package at.fhv.se.collabnotes.smoketests.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.collabnotes.application.CreateNoteService;
import at.fhv.se.collabnotes.application.EventProcessingService;
import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.ViewNoteService;
import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;

@SpringBootTest
public class ApplicationControllerSmokeTests {

    @Autowired
	private ViewNoteService viewNoteService;

	@Autowired
	private CreateNoteService createNoteService;

	@Autowired
	private StatisticsService statService;

	@Autowired
	private EventProcessingService eventProcService;

	@MockBean
	private NoteRepository noteRepository;

	@MockBean
    private StatisticsRepository statsRepo;
	
	@Test
	public void given_applicationcontrollers_when_inejcted_then_allnotnull() {
		assertNotNull(viewNoteService);
		assertNotNull(createNoteService);
		assertNotNull(statService);
		assertNotNull(eventProcService);
	}
}
