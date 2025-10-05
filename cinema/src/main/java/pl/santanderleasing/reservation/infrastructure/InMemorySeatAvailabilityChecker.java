package pl.santanderleasing.reservation.infrastructure;

import pl.santanderleasing.reservation.domain.SeatAvailabilityChecker;
import pl.santanderleasing.reservation.domain.SeatPosition;
import pl.santanderleasing.reservation.domain.ShowTimeId;

import java.util.List;

public class InMemorySeatAvailabilityChecker implements SeatAvailabilityChecker {
    @Override
    public boolean areSeatsAvailable(ShowTimeId showTimeId, List<SeatPosition> seatPositions) {
        return true;
    }
}
