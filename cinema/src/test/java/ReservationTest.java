import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.santanderleasing.commons.DomainEvent;
import pl.santanderleasing.commons.ReservationCancelledEvent;
import pl.santanderleasing.commons.ReservationConfirmedEvent;
import pl.santanderleasing.commons.TicketsReservedEvent;
import pl.santanderleasing.reservation.domain.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReservationTest {

    private SeatAvailabilityChecker availabilityChecker;
    private LocalDateTime screeningTime;
    private ShowTimeId showTimeId;
    private String userId;
    private List<SeatPosition> seatPositions;

    @BeforeEach
    void setUp() {
        availabilityChecker = Mockito.mock(SeatAvailabilityChecker.class);
        showTimeId = ShowTimeId.of("show-1");
        screeningTime = LocalDateTime.now().plusHours(2);
        userId = "user123";

        seatPositions = List.of(
                SeatPosition.of(1, 1),
                SeatPosition.of(1, 2)
        );
    }

    @Test
    void should_create_reservation() {
        when(availabilityChecker.areSeatsAvailable(showTimeId, seatPositions)).thenReturn(true);

        Reservation reservation = Reservation.create(userId, showTimeId, screeningTime, seatPositions, availabilityChecker);
        assertEquals(ReservationStatus.SUBMITTED, reservation.getStatus());

        List<DomainEvent> events = reservation.getAndClearDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(TicketsReservedEvent.class, events.get(0));
    }

    @Test
    void should_throw_exception_if_seats_not_available() {
        when(availabilityChecker.areSeatsAvailable(showTimeId, seatPositions)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                Reservation.create(userId, showTimeId, screeningTime, seatPositions, availabilityChecker));

        assertEquals("One or more seats are not available", ex.getMessage());
    }

    @Test
    void should_throw_exception_if_seats_are_empty() {
        when(availabilityChecker.areSeatsAvailable(showTimeId, List.of())).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                Reservation.create(userId, showTimeId, screeningTime, List.of(), availabilityChecker));

        assertEquals("Reservation must have at least one seat", ex.getMessage());
    }

    @Test
    void should_reconstitute_reservation() {
        List<ReservedSeat> reservedSeats = seatPositions.stream()
                .map(ReservedSeat::create)
                .toList();

        Reservation reservation = Reservation.reconstitute(
                ReservationId.generate(),
                userId,
                showTimeId,
                screeningTime,
                reservedSeats,
                ReservationStatus.CONFIRMED,
                Instant.now(),
                0L);

        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    void should_confirm() {
        when(availabilityChecker.areSeatsAvailable(showTimeId, seatPositions)).thenReturn(true);

        Reservation reservation = Reservation.create(userId, showTimeId, screeningTime, seatPositions, availabilityChecker);
        assertEquals(ReservationStatus.SUBMITTED, reservation.getStatus());

        reservation.confirm();
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

        List<DomainEvent> events = reservation.getAndClearDomainEvents();
        assertEquals(2, events.size());
        assertTrue(events.stream().anyMatch(e -> e instanceof ReservationConfirmedEvent));
    }

    @Test
    void should_throw_if_confirm_when_not_submitted() {
        when(availabilityChecker.areSeatsAvailable(showTimeId, seatPositions)).thenReturn(true);

        Reservation reservation = Reservation.create(userId, showTimeId, screeningTime, seatPositions, availabilityChecker);
        reservation.confirm();

        IllegalStateException ex = assertThrows(IllegalStateException.class, reservation::confirm);
        assertEquals("Reservation can only be reserved from SUBMITTED state", ex.getMessage());
    }

    @Test
    void should_cancel_reservation_with_full_refund() {
        List<ReservedSeat> reservedSeats = seatPositions.stream()
                .map(ReservedSeat::create)
                .toList();

        Reservation reservation = Reservation.reconstitute(
                ReservationId.generate(),
                userId,
                showTimeId,
                screeningTime,
                reservedSeats,
                ReservationStatus.CONFIRMED,
                Instant.now(),
                0L);

        reservation.cancel(true);
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());

        List<DomainEvent> events = reservation.getAndClearDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(ReservationCancelledEvent.class, events.get(0));

        ReservationCancelledEvent event = (ReservationCancelledEvent) events.get(0);
        assertTrue(event.fullRefund());
    }

    @Test
    void should_cancel_reservation_without_full_refund() {
        LocalDateTime closeShowTime = LocalDateTime.now().plusMinutes(30);

        List<ReservedSeat> reservedSeats = seatPositions.stream()
                .map(ReservedSeat::create)
                .toList();

        Reservation reservation = Reservation.reconstitute(
                ReservationId.generate(),
                userId,
                showTimeId,
                closeShowTime,
                reservedSeats,
                ReservationStatus.CONFIRMED,
                Instant.now(),
                0L);

        reservation.cancel(false);
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());

        List<DomainEvent> events = reservation.getAndClearDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(ReservationCancelledEvent.class, events.get(0));

        ReservationCancelledEvent event = (ReservationCancelledEvent) events.get(0);
        assertFalse(event.fullRefund());
    }

    @Test
    void should_throw_if_try_to_cancel_not_reserved_reservation() {
        List<ReservedSeat> reservedSeats = seatPositions.stream()
                .map(ReservedSeat::create)
                .toList();

        Reservation reservation = Reservation.reconstitute(
                ReservationId.generate(),
                userId,
                showTimeId,
                screeningTime,
                reservedSeats,
                ReservationStatus.CANCELLED,
                Instant.now(),
                0L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> reservation.cancel(true));
        assertEquals("Cannot cancel reservation that is not in RESERVED status", ex.getMessage());
    }
}
