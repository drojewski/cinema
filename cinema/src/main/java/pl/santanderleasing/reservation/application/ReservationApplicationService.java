package pl.santanderleasing.reservation.application;

import pl.santanderleasing.reservation.domain.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationApplicationService {

    private final ReservationRepository reservationRepository;
    private final SeatAvailabilityChecker seatAvailabilityChecker;
    private final LoyaltyProgramService loyaltyProgramService;
    private final ShowTimeRepository showTimeRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ReservationApplicationService(
            ReservationRepository reservationRepository,
            SeatAvailabilityChecker seatAvailabilityChecker,
            LoyaltyProgramService loyaltyProgramService,
            ShowTimeRepository showTimeRepository,
            DomainEventPublisher domainEventPublisher) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "ReservationRepository cannot be null");
        this.seatAvailabilityChecker = Objects.requireNonNull(seatAvailabilityChecker, "SeatAvailabilityChecker cannot be null");
        this.loyaltyProgramService = Objects.requireNonNull(loyaltyProgramService, "LoyaltyProgramService cannot be null");
        this.showTimeRepository = Objects.requireNonNull(showTimeRepository, "ShowTimeRepository cannot be null");
        this.domainEventPublisher = Objects.requireNonNull(domainEventPublisher, "DomainEventPublisher cannot be null");
    }

    // tx
    // todo: optimistic locking
    public ReservationId reserveTickets(ReserveTicketsCommand command) {
        Objects.requireNonNull(command, "ReserveTicketsCommand cannot be null");

        LocalDateTime screeningTime = showTimeRepository.findScreeningTimeBy(command.showTimeId());

        Reservation reservation = Reservation.create(
                command.userId(),
                command.showTimeId(),
                screeningTime,
                command.seatPositions(),
                seatAvailabilityChecker
        );

        reservationRepository.save(reservation);
        publishDomainEvents(reservation);

        return reservation.getId();
    }

    // tx
    public void cancelReservation(CancelReservationCommand command) {
        Objects.requireNonNull(command, "CancelReservationCommand cannot be null");

        Reservation reservation = reservationRepository.findBy(command.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + command.reservationId()));

        if (!reservation.getUserId().equals(command.userId())) {
            throw new IllegalArgumentException("Reservation ownership mismatch 8-O");
        }

        // may be moved to domain service if logic becomes more complex
        boolean isPowerVIPClient = loyaltyProgramService.isPowerVIPClient(command.userId());

        reservation.cancel(isPowerVIPClient);

        reservationRepository.save(reservation);
        publishDomainEvents(reservation);
    }

    private void publishDomainEvents(Reservation reservation) {
        reservation.getAndClearDomainEvents().forEach(domainEventPublisher::publish);
    }
}