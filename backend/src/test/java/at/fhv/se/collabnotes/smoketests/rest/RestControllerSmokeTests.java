package at.fhv.se.collabnotes.smoketests.rest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import at.fhv.se.collabnotes.rest.NoteRestController;
import at.fhv.se.collabnotes.rest.StatisticsRestController;

@SpringBootTest
class RestControllerSmokeTests {

	@Autowired
	private NoteRestController noteRestController;
    
    @Autowired
	private StatisticsRestController statsRestController;

	@Test
	public void given_restcontrollers_when_inejcted_then_allnotnull() {
		assertNotNull(noteRestController);
        assertNotNull(statsRestController);
	}
}
