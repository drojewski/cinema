package pl.santanderleasing.reservation.application;

import pl.santanderleasing.reservation.domain.ReservationId;

import java.util.Objects;

public record CancelReservationCommand(
        ReservationId reservationId,
        String userId
) {
    public CancelReservationCommand {
        Objects.requireNonNull(reservationId, "ReservationId cannot be null");
        Objects.requireNonNull(userId, "UserId cannot be null");

        if (userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }
    }
}