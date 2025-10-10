package pl.santanderleasing.payment.application;

import pl.santanderleasing.commons.ReservationCreatedEvent;

public class ReservationCreatedEventHandler {
    private final PaymentService paymentService;

    public ReservationCreatedEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void handle(ReservationCreatedEvent event) {
        paymentService.makePayment(event.reservationId());
    }
}