package pl.santanderleasing.reservation.domain;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}