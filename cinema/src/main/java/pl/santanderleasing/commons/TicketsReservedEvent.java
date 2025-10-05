package pl.santanderleasing.commons;

import pl.santanderleasing.reservation.domain.SeatPosition;
import pl.santanderleasing.reservation.domain.ShowTimeId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record TicketsReservedEvent(
        String reservationId,
        String userId,
        ShowTimeId showTimeId,
        List<SeatPosition> seatPositions,
        Instant occurredOn) implements DomainEvent {
    public TicketsReservedEvent {
        Objects.requireNonNull(reservationId, "ReservationId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(showTimeId, "ShowTimeId cannot be null");
        Objects.requireNonNull(seatPositions, "SeatPositions cannot be null");
        Objects.requireNonNull(occurredOn, "OccurredOn cannot be null");
    }

    public static TicketsReservedEvent create(
            String reservationId,
            String userId,
            ShowTimeId showTimeId,
            List<SeatPosition> seatPositions
    ) {
        return new TicketsReservedEvent(
                reservationId,
                userId,
                showTimeId,
                seatPositions,
                Instant.now()
        );
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