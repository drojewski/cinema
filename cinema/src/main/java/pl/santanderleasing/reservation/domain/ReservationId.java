package pl.santanderleasing.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record ReservationId(String value) {

    public ReservationId {
        Objects.requireNonNull(value, "ReservationId cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("ReservationId cannot be empty");
        }
    }

    public static ReservationId generate() {
        return new ReservationId(UUID.randomUUID().toString());
    }

    public static ReservationId of(String value) {
        return new ReservationId(value);
    }
}