package pl.santanderleasing.reservation.infrastructure;

import pl.santanderleasing.reservation.domain.Reservation;
import pl.santanderleasing.reservation.domain.ReservationId;
import pl.santanderleasing.reservation.domain.ReservationRepository;

import java.util.Optional;

public class InMemoryReservationRepository implements ReservationRepository {
    @Override
    public void save(Reservation reservation) {

    }

    @Override
    public Optional<Reservation> findBy(ReservationId reservationId) {
        return Optional.empty();
    }
}
