package pl.santanderleasing.commons;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}
