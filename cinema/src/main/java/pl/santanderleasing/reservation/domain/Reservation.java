package pl.santanderleasing.reservation.domain;

import pl.santanderleasing.commons.DomainEvent;
import pl.santanderleasing.commons.ReservationCancelledEvent;
import pl.santanderleasing.commons.ReservationPaidEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Reservation {

    private final ReservationId id;
    private final String userId;
    private final ShowTimeId showTimeId;
    private final LocalDateTime screeningTime;
    private final List<ReservedSeat> reservedSeats;
    private ReservationStatus status;
    private final Instant createdAt;
    private final List<DomainEvent> domainEvents;
    private long version;

    Reservation(
            ReservationId id,
            String userId,
            ShowTimeId showTimeId,
            LocalDateTime screeningTime,
            List<ReservedSeat> reservedSeats,
            ReservationStatus status,
            Instant createdAt,
            long version) {
        this.id = Objects.requireNonNull(id, "ReservationId cannot be null");
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.showTimeId = Objects.requireNonNull(showTimeId, "ShowTimeId cannot be null");
        this.screeningTime = Objects.requireNonNull(screeningTime, "ScreeningTime cannot be null");
        this.reservedSeats = List.copyOf(Objects.requireNonNull(reservedSeats, "ReservedSeats cannot be null"));
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.domainEvents = new ArrayList<>();
        this.version = version;
        ensureReservationCoreRulesAreRespected();
    }

    // invariants - rules controlled by aggregate, no ext deps needed
    private void ensureReservationCoreRulesAreRespected() {
        if (reservedSeats.isEmpty()) {
            throw new IllegalStateException("Reservation must have at least one seat");
        }

        if (reservedSeats.size() > 2) {
            throw new IllegalStateException("Reservation cannot have more than 2 seats");
        }
    }

    public void confirm() {
        if (this.status != ReservationStatus.CREATED) {
            throw new IllegalStateException("Reservation can only be reserved from CREATED state");
        }
        this.status = ReservationStatus.PAID;
        addDomainEvent(new ReservationPaidEvent(getId().value(), Instant.now()));
        incrementVersion();
    }

    public void cancel(boolean isEntitledToCancelWithFullRefund) {
        if (status != ReservationStatus.PAID) {
            throw new IllegalStateException("Cannot cancel reservation that is not in PAID status");
        }

        boolean fullRefund = isEntitledToCancelWithFullRefund || !isLessThanOneHourBefore(LocalDateTime.now());
        this.status = ReservationStatus.CANCELLED;
        addDomainEvent(ReservationCancelledEvent.create(id, userId, showTimeId, fullRefund));
        incrementVersion();
    }

    private boolean isLessThanOneHourBefore(LocalDateTime currentTime) {
        return screeningTime.isBefore(currentTime.plusHours(1));
    }

    void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getAndClearDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    public static Reservation reconstitute(
            ReservationId id,
            String userId,
            ShowTimeId showTimeId,
            LocalDateTime screeningTime,
            List<ReservedSeat> reservedSeats,
            ReservationStatus status,
            Instant createdAt,
            long version) {
        return new Reservation(id, userId, showTimeId, screeningTime, reservedSeats, status, createdAt, version);
    }

    private void incrementVersion() {
        this.version++;
    }

    public ReservationId getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
