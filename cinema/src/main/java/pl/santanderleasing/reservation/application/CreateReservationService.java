package pl.santanderleasing.reservation.application;

import pl.santanderleasing.commons.DomainEventPublisher;
import pl.santanderleasing.reservation.domain.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class CreateReservationService {

    private final ReservationRepository reservationRepository;
    private final LoyaltyProgramService loyaltyProgramService;
    private final ShowTimeRepository showTimeRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final ReservationFactory reservationFactory;

    public CreateReservationService(
            ReservationRepository reservationRepository,
            LoyaltyProgramService loyaltyProgramService,
            ShowTimeRepository showTimeRepository,
            DomainEventPublisher domainEventPublisher,
            ReservationFactory reservationFactory) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "ReservationRepository cannot be null");
        this.loyaltyProgramService = Objects.requireNonNull(loyaltyProgramService, "LoyaltyProgramService cannot be null");
        this.showTimeRepository = Objects.requireNonNull(showTimeRepository, "ShowTimeRepository cannot be null");
        this.domainEventPublisher = Objects.requireNonNull(domainEventPublisher, "DomainEventPublisher cannot be null");
        this.reservationFactory = Objects.requireNonNull(reservationFactory, "ReservationFactory cannot be null");
    }

    public ReservationId create(CreateReservationCommand command) {
        Objects.requireNonNull(command, "CreateReservationCommand cannot be null");
        LocalDateTime screeningTime = showTimeRepository.findScreeningTimeBy(command.showTimeId());
        Reservation reservation = reservationFactory.create(
                command.userId(),
                command.showTimeId(),
                screeningTime,
                command.seatPositions());
        reservationRepository.save(reservation);
        publishDomainEvents(reservation);
        return reservation.getId();
    }

    public void cancel(CancelReservationCommand command) {
        Objects.requireNonNull(command, "CancelReservationCommand cannot be null");
        Reservation reservation = reservationRepository.findBy(command.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + command.reservationId()));

        if (!reservation.getUserId().equals(command.userId())) {
            throw new IllegalArgumentException("Reservation ownership mismatch :-(");
        }

        boolean isEntitledToCancelWithFullRefund = loyaltyProgramService.isCinemaCardGoldOwner(command.userId());
        reservation.cancel(isEntitledToCancelWithFullRefund);
        reservationRepository.save(reservation);
        publishDomainEvents(reservation);
    }

    private void publishDomainEvents(Reservation reservation) {
        reservation.getAndClearDomainEvents().forEach(domainEventPublisher::publish);
    }
}
