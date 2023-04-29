package at.fhv.se.collabnotes.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.fhv.se.collabnotes.application.StatisticsService;
import at.fhv.se.collabnotes.application.dto.StatisticsDTO;

@RestController
@RequestMapping("/rest")
public class StatisticsRestController {
    private static final String STATS_ENDPOINT = "/stats";

    @Autowired
    private StatisticsService statsService;

    @GetMapping(STATS_ENDPOINT)
    public StatisticsDTO stats() {
        return statsService.statsInfo();
    }
}
