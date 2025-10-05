package pl.santanderleasing.reservation.domain;

import java.util.List;

public interface SeatAvailabilityChecker {
    boolean areSeatsAvailable(ShowTimeId showTimeId, List<SeatPosition> seatPositions);
}