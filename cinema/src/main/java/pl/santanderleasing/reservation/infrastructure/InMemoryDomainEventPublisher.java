package pl.santanderleasing.reservation.infrastructure;

import pl.santanderleasing.reservation.domain.DomainEvent;
import pl.santanderleasing.reservation.domain.DomainEventPublisher;

public class InMemoryDomainEventPublisher implements DomainEventPublisher {
    @Override
    public void publish(DomainEvent event) {
        System.out.println("Publishing " + event);
    }
}
