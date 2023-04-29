package at.fhv.se.collabnotes.domain.events;

import java.util.ArrayList;
import java.util.List;

public abstract class DomainEventPublisher {
    private List<DomainEventSubscriber<?>> subscribers;

    public <T extends DomainEvent> void subscribe(DomainEventSubscriber<T> s) {
        List<DomainEventSubscriber<?>> subs = this.subscribers();
        subs.add(s);
    }

    @SuppressWarnings("unchecked")
    protected void publish(final DomainEvent event) {
        Class<?> eventType = event.getClass();
        List<DomainEventSubscriber<?>> subs = this.subscribers();

        for (DomainEventSubscriber<?> s : subs) {
            DomainEventSubscriber<DomainEvent> sTyped = (DomainEventSubscriber<DomainEvent>) s;
            Class<?> subscribedTo = s.subscribedToEventType();
            if (subscribedTo == eventType || subscribedTo == DomainEvent.class) {
                sTyped.handleEvent(event);
            }
        }
    }

    private List<DomainEventSubscriber<?>> subscribers() {
        if (null == subscribers) {
            subscribers = new ArrayList<>();
        }

        return subscribers;
    }
}
