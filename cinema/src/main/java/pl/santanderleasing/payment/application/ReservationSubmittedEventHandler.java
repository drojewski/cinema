package pl.santanderleasing.payment.application;

import pl.santanderleasing.commons.ReservationSubmittedEvent;

public class ReservationSubmittedEventHandler {
    private final PaymentService paymentService;

    public ReservationSubmittedEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void handle(ReservationSubmittedEvent event) {
        paymentService.makePayment(event.reservationId());
    }
}