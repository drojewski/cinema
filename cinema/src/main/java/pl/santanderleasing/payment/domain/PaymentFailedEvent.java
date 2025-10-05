package pl.santanderleasing.payment.domain;

import pl.santanderleasing.commons.DomainEvent;

import java.time.Instant;

public class PaymentFailedEvent implements DomainEvent {
    private final String reservationId;

    public PaymentFailedEvent(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() { return reservationId; }

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }
}