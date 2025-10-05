package pl.santanderleasing.commons;

public class InMemoryDomainEventPublisher implements DomainEventPublisher {
    @Override
    public void publish(DomainEvent event) {
        System.out.println("Publishing " + event);
    }
}
