package at.fhv.se.collabnotes.infrastructure;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.fhv.se.collabnotes.domain.events.DomainEvent;

// NOTE: mapped it with annotations because it is a pure DB entitiy
@Entity
@Table(name = "persisted_events") 
public class PersistedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date created;
    private String type;
    private boolean consumed;
    private String payload;

    public PersistedEvent(Date created, String type, String payload) {
        this.created = created;
        this.type = type;
        this.payload = payload;
    }

    public Long id() {
        return this.id;
    }

    public Date created() {
        return created;
    }

    public String type() {
        return type;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public <T extends DomainEvent> Optional<T> domainEventOf(Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        try {
            T evt = objectMapper.readValue(this.payload, clazz);
            return Optional.of(evt);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }

    // NOTE: needed by Hibernate
    @SuppressWarnings("unused")
    private PersistedEvent() {
    }
}
