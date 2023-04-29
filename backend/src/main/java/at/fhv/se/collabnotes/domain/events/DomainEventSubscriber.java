package at.fhv.se.collabnotes.domain.events;

public interface DomainEventSubscriber<T extends DomainEvent> {
    public Class<?> subscribedToEventType();
    public void handleEvent(T domainEvent);
}
