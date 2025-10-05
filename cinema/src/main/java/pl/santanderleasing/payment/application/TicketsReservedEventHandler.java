package pl.santanderleasing.payment.application;

import pl.santanderleasing.commons.TicketsReservedEvent;

public class TicketsReservedEventHandler {
    private final PaymentService paymentService;

    public TicketsReservedEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void handle(TicketsReservedEvent event) {
        paymentService.makePayment(event.reservationId());
    }
}