package pl.santanderleasing.reservation.application;

import pl.santanderleasing.reservation.domain.SeatPosition;
import pl.santanderleasing.reservation.domain.ShowTimeId;

import java.util.List;
import java.util.Objects;

public record CreateReservationCommand(
        String userId,
        ShowTimeId showTimeId,
        List<SeatPosition> seatPositions
) {
    public CreateReservationCommand {
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(showTimeId, "ShowTimeId cannot be null");
        Objects.requireNonNull(seatPositions, "SeatPositions cannot be null");

        if (userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be empty");
        }

        if (seatPositions.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        if (seatPositions.size() > 2) {
            throw new IllegalArgumentException("Cannot reserve more than 2 seats");
        }
    }
}