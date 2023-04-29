package at.fhv.se.collabnotes.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
     value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
  )
@Component
@EnableScheduling
@Async
public class AsyncProcessing {

    private static final double EVENT_PROCESSING_FREQUENCY = 2;

    @Autowired
    private EventProcessingService eventProc;

    @Scheduled(fixedRate = (int) (1000.0 / EVENT_PROCESSING_FREQUENCY))
    public void runEventProcessing() {
        // TODO: process all events per schedule, but each in separate TX
        eventProc.processNextEvent();
    }
}
