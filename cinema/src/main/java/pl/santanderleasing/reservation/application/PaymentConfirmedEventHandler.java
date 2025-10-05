package pl.santanderleasing.reservation.application;

import pl.santanderleasing.commons.DomainEventPublisher;
import pl.santanderleasing.payment.domain.PaymentConfirmedEvent;
import pl.santanderleasing.reservation.domain.Reservation;
import pl.santanderleasing.reservation.domain.ReservationId;
import pl.santanderleasing.reservation.domain.ReservationRepository;

import java.util.Optional;

public class PaymentConfirmedEventHandler {

    private final ReservationRepository reservationRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PaymentConfirmedEventHandler(ReservationRepository reservationRepository,
                                        DomainEventPublisher domainEventPublisher) {
        this.reservationRepository = reservationRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    // tx
    public void handle(PaymentConfirmedEvent event) {
        Optional<Reservation> reservationOpt = reservationRepository.findBy(ReservationId.of(event.getReservationId()));

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.completeReservation();
            reservationRepository.save(reservation);
            reservation.getAndClearDomainEvents().forEach(domainEventPublisher::publish);
        } else {
            // ...
        }
    }
}
