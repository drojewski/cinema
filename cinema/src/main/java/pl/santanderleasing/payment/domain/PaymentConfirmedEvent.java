package pl.santanderleasing.payment.domain;

import pl.santanderleasing.commons.DomainEvent;

import java.time.Instant;

public class PaymentConfirmedEvent implements DomainEvent {
    private final String reservationId;

    public PaymentConfirmedEvent(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() { return reservationId; }

    @Override
    public Instant occurredAt() {
        return Instant.now();
    }
}
