package pl.santanderleasing.commons;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}