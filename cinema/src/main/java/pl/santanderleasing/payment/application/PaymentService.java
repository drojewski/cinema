package pl.santanderleasing.payment.application;

import pl.santanderleasing.commons.DomainEventPublisher;
import pl.santanderleasing.payment.domain.PaymentConfirmedEvent;

public class PaymentService {
    private final DomainEventPublisher domainEventPublisher;

    public PaymentService(DomainEventPublisher domainEventPublisher) {
        this.domainEventPublisher = domainEventPublisher;
    }

    //tx
    public void makePayment(String reservationId) {
        // ..
       domainEventPublisher.publish(new PaymentConfirmedEvent(reservationId));
    }
}
