package pl.santanderleasing.commons;

import java.time.Instant;
import java.util.Objects;

public record ReservationConfirmedEvent(
        String reservationId,
        Instant occurredOn) implements DomainEvent {
    public ReservationConfirmedEvent {
        Objects.requireNonNull(reservationId, "ReservationId cannot be null");
        Objects.requireNonNull(occurredOn, "OccurredOn cannot be null");
    }

    public static ReservationConfirmedEvent create(
            String reservationId) {
        return new ReservationConfirmedEvent(reservationId, Instant.now());
    }

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }

    @Override
    public String reservationId() {
        return reservationId;
    }
}