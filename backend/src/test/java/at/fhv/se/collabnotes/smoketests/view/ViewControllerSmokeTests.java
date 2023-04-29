package at.fhv.se.collabnotes.smoketests.view;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import at.fhv.se.collabnotes.domain.repositories.NoteRepository;
import at.fhv.se.collabnotes.domain.repositories.StatisticsRepository;
import at.fhv.se.collabnotes.view.NoteViewController;

@SpringBootTest
class ViewControllerSmokeTests {

	@Autowired
	private NoteViewController notesViewController;

	@MockBean
	private NoteRepository noteRepository;

	@MockBean
    private StatisticsRepository statsRepo;
	
	@Test
	public void given_viewcontrollers_when_inejcted_then_allnotnull() {
		assertNotNull(notesViewController);
	}
}
