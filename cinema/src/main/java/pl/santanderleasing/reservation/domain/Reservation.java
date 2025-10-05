package pl.santanderleasing.reservation.domain;

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

    private Reservation(
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
        // validate core aggregate rules - internal consistency checks without external dependencies
        ensureReservationCoreRulesAreRespected();

    }

    public static Reservation create(
            String userId,
            ShowTimeId showTimeId,
            LocalDateTime screeningTime,
            List<SeatPosition> seatPositions,
            SeatAvailabilityChecker availabilityChecker) {

        Objects.requireNonNull(seatPositions, "SeatPositions cannot be null");
        Objects.requireNonNull(availabilityChecker, "SeatAvailabilityChecker cannot be null");

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
                ReservationStatus.SUBMITTED,
                Instant.now(),
                0L);

        reservation.addDomainEvent(
                TicketsReservedEvent.create(
                        reservation.id,
                        userId,
                        showTimeId,
                        seatPositions
                )
        );
        return reservation;
    }

    // aggregate lifecycle - chapter 6
    public static Reservation reconstitute(
            ReservationId id,
            String userId,
            ShowTimeId showTimeId,
            LocalDateTime screeningTime,
            List<ReservedSeat> reservedSeats,
            ReservationStatus status,
            Instant createdAt, long version) {
        return new Reservation(id, userId, showTimeId, screeningTime, reservedSeats, status, createdAt, version);
    }

    public void cancel(boolean isPowerVIPClient) {
        if (status != ReservationStatus.RESERVED) {
            throw new IllegalStateException("Cannot cancel reservation that is not in RESERVED status");
        }

        boolean isLessThanOneHour = isLessThanOneHourBefore(LocalDateTime.now());
        boolean fullRefund = isPowerVIPClient || !isLessThanOneHour;

        addDomainEvent(ReservationCancelledEvent.create(id, userId, showTimeId, fullRefund));
        this.status = ReservationStatus.CANCELLED;
        incrementVersion();
    }

    // invariants - specific rules that can be verified by the aggregate's own knowledge without external deps
    private void ensureReservationCoreRulesAreRespected() {
        if (reservedSeats.isEmpty()) {
            throw new IllegalStateException("Reservation must have at least one seat");
        }

        if (reservedSeats.size() > 2) {
            throw new IllegalStateException("Reservation cannot have more than 2 seats");
        }
    }

    private void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getAndClearDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
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

    public long getVersion() {
        return version;
    }

    private boolean isLessThanOneHourBefore(LocalDateTime currentTime) {
        return screeningTime.isBefore(currentTime.plusHours(1));
    }

    private void incrementVersion() {
        this.version++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}