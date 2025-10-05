package pl.santanderleasing.reservation.domain;

import java.time.Instant;
import java.util.Objects;

public record ReservationCancelledEvent(
        ReservationId reservationId,
        String userId,
        ShowTimeId showTimeId,
        boolean fullRefund,
        Instant occurredOn) implements DomainEvent {
    public ReservationCancelledEvent {
        Objects.requireNonNull(reservationId, "ReservationId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(showTimeId, "ShowTimeId cannot be null");
        Objects.requireNonNull(occurredOn, "OccurredOn cannot be null");
    }

    public static ReservationCancelledEvent create(
            ReservationId reservationId,
            String userId,
            ShowTimeId showTimeId,
            boolean fullRefund
    ) {
        return new ReservationCancelledEvent(
                reservationId,
                userId,
                showTimeId,
                fullRefund,
                Instant.now()
        );
    }

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }
}