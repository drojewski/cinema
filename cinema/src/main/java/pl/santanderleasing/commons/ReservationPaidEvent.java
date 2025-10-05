package pl.santanderleasing.commons;

import java.time.Instant;
import java.util.Objects;

public record ReservationPaidEvent(
        String reservationId,
        Instant occurredOn) implements DomainEvent {
    public ReservationPaidEvent {
        Objects.requireNonNull(reservationId, "ReservationId cannot be null");
        Objects.requireNonNull(occurredOn, "OccurredOn cannot be null");
    }

    public static ReservationPaidEvent create(
            String reservationId) {
        return new ReservationPaidEvent(reservationId, Instant.now());
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