package pl.santanderleasing.reservation.domain;

import java.util.Optional;

public interface ReservationRepository {
    void save(Reservation reservation);
    Optional<Reservation> findBy(ReservationId reservationId);
}