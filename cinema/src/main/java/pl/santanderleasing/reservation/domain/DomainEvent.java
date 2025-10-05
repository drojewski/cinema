package pl.santanderleasing.reservation.domain;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}
