package pl.santanderleasing.reservation.domain;

import pl.santanderleasing.commons.ReservationCreatedEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ReservationFactory {

    private final SeatAvailabilityChecker availabilityChecker;

    public ReservationFactory(SeatAvailabilityChecker availabilityChecker) {
        this.availabilityChecker = Objects.requireNonNull(availabilityChecker);
    }

    public Reservation create(
            String userId,
            ShowTimeId showTimeId,
            LocalDateTime screeningTime,
            List<SeatPosition> seatPositions) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(showTimeId);
        Objects.requireNonNull(seatPositions);

        if (!availabilityChecker.areSeatsAvailable(showTimeId, seatPositions)) {
            throw new IllegalStateException("One or more seats are not available");
        }

        List<ReservedSeat> seats = seatPositions.stream()
                .map(ReservedSeat::create)
                .toList();

        Reservation reservation = new Reservation(
                ReservationId.generate(),
                userId,
                showTimeId,
                screeningTime,
                seats,
                ReservationStatus.CREATED,
                Instant.now(),
                0L);

        reservation.addDomainEvent(
                ReservationCreatedEvent.create(
                        reservation.getId().value(),
                        userId,
                        showTimeId,
                        seatPositions
                )
        );

        return reservation;
    }
}
